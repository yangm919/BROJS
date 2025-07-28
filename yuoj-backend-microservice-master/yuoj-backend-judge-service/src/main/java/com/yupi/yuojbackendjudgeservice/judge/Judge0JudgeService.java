package com.yupi.yuojbackendjudgeservice.judge;

import cn.hutool.json.JSONUtil;
import com.yupi.yuojbackendcommon.common.ErrorCode;
import com.yupi.yuojbackendcommon.exception.BusinessException;
import com.yupi.yuojbackendjudgeservice.judge.codesandbox.impl.ThirdPartyCodeSandbox;
import com.yupi.yuojbackendmodel.model.codesandbox.ExecuteCodeRequest;
import com.yupi.yuojbackendmodel.model.codesandbox.ExecuteCodeResponse;
import com.yupi.yuojbackendmodel.model.codesandbox.JudgeInfo;
import com.yupi.yuojbackendmodel.model.dto.question.JudgeCase;
import com.yupi.yuojbackendmodel.model.entity.Question;
import com.yupi.yuojbackendmodel.model.entity.QuestionSubmit;
import com.yupi.yuojbackendmodel.model.enums.QuestionSubmitStatusEnum;
import com.yupi.yuojbackendserviceclient.service.QuestionFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 使用Judge0 API的判题服务
 */
@Slf4j
@Service
public class Judge0JudgeService {

    @Resource
    private QuestionFeignClient questionFeignClient;

    @Resource
    private ThirdPartyCodeSandbox codeSandbox;

    /**
     * 使用Judge0 API进行判题
     * @param questionSubmitId 题目提交ID
     * @return 判题结果
     */
    public QuestionSubmit doJudgeWithJudge0(long questionSubmitId) {
        log.info("开始使用Judge0 API判题，题目提交ID: {}", questionSubmitId);

        // 1. 获取题目提交信息
        QuestionSubmit questionSubmit = questionFeignClient.getQuestionSubmitById(questionSubmitId);
        if (questionSubmit == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "提交信息不存在");
        }

        // 2. 获取题目信息
        Long questionId = questionSubmit.getQuestionId();
        Question question = questionFeignClient.getQuestionById(questionId);
        if (question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "题目不存在");
        }

        // 3. 检查提交状态
        if (!questionSubmit.getStatus().equals(QuestionSubmitStatusEnum.WAITING.getValue())) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "题目正在判题中");
        }

        // 4. 更新状态为判题中
        updateQuestionSubmitStatus(questionSubmitId, QuestionSubmitStatusEnum.RUNNING);

        try {
            // 5. 执行代码
            ExecuteCodeResponse executeCodeResponse = executeCode(questionSubmit, question);

            // 6. 判题
            JudgeInfo judgeInfo = judgeCode(executeCodeResponse, question);

            // 7. 更新判题结果
            updateJudgeResult(questionSubmitId, judgeInfo);

            // 8. 返回结果
            return questionFeignClient.getQuestionSubmitById(questionSubmitId);

        } catch (Exception e) {
            log.error("Judge0判题失败", e);
            // 更新状态为失败
            updateQuestionSubmitStatus(questionSubmitId, QuestionSubmitStatusEnum.FAILED);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "判题失败: " + e.getMessage());
        }
    }

    /**
     * 执行代码
     */
    private ExecuteCodeResponse executeCode(QuestionSubmit questionSubmit, Question question) {
        String language = questionSubmit.getLanguage();
        String code = questionSubmit.getCode();

        // 获取测试用例
        String judgeCaseStr = question.getJudgeCase();
        List<JudgeCase> judgeCaseList = JSONUtil.toList(judgeCaseStr, JudgeCase.class);
        List<String> inputList = judgeCaseList.stream()
                .map(JudgeCase::getInput)
                .collect(Collectors.toList());

        // 构建执行请求
        ExecuteCodeRequest executeCodeRequest = ExecuteCodeRequest.builder()
                .code(code)
                .language(language)
                .inputList(inputList)
                .build();

        log.info("开始执行代码，语言: {}, 输入用例数: {}", language, inputList.size());

        // 执行代码
        ExecuteCodeResponse executeCodeResponse = codeSandbox.executeCode(executeCodeRequest);

        log.info("代码执行完成，状态: {}, 消息: {}", 
                executeCodeResponse.getStatus(), 
                executeCodeResponse.getMessage());

        return executeCodeResponse;
    }

    /**
     * 判题逻辑
     */
    public JudgeInfo judgeCode(ExecuteCodeResponse executeCodeResponse, Question question) {
        JudgeInfo judgeInfo = executeCodeResponse.getJudgeInfo();
        List<String> outputList = executeCodeResponse.getOutputList();

        // 获取期望输出
        String judgeCaseStr = question.getJudgeCase();
        List<JudgeCase> judgeCaseList = JSONUtil.toList(judgeCaseStr, JudgeCase.class);
        List<String> expectedOutputList = judgeCaseList.stream()
                .map(JudgeCase::getOutput)
                .collect(Collectors.toList());

        // 检查执行状态
        if (executeCodeResponse.getStatus() != 0) {
            // 执行失败
            judgeInfo.setMessage("执行失败: " + executeCodeResponse.getMessage());
            return judgeInfo;
        }

        // 检查输出数量
        if (outputList.size() != expectedOutputList.size()) {
            judgeInfo.setMessage("输出数量不匹配，期望: " + expectedOutputList.size() + 
                    ", 实际: " + outputList.size());
            return judgeInfo;
        }

        // 逐行比较输出
        for (int i = 0; i < outputList.size(); i++) {
            String expectedOutput = expectedOutputList.get(i).trim();
            String actualOutput = outputList.get(i).trim();

            if (!expectedOutput.equals(actualOutput)) {
                judgeInfo.setMessage("答案错误，第" + (i + 1) + "个测试用例不通过\n" +
                        "期望: " + expectedOutput + "\n" +
                        "实际: " + actualOutput);
                return judgeInfo;
            }
        }

        // 所有测试用例通过
        judgeInfo.setMessage("答案正确");
        return judgeInfo;
    }

    /**
     * 更新题目提交状态
     */
    private void updateQuestionSubmitStatus(long questionSubmitId, QuestionSubmitStatusEnum status) {
        QuestionSubmit questionSubmitUpdate = new QuestionSubmit();
        questionSubmitUpdate.setId(questionSubmitId);
        questionSubmitUpdate.setStatus(status.getValue());
        
        boolean update = questionFeignClient.updateQuestionSubmitById(questionSubmitUpdate);
        if (!update) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "题目状态更新错误");
        }
    }

    /**
     * 更新判题结果
     */
    private void updateJudgeResult(long questionSubmitId, JudgeInfo judgeInfo) {
        QuestionSubmit questionSubmitUpdate = new QuestionSubmit();
        questionSubmitUpdate.setId(questionSubmitId);
        questionSubmitUpdate.setStatus(QuestionSubmitStatusEnum.SUCCEED.getValue());
        questionSubmitUpdate.setJudgeInfo(JSONUtil.toJsonStr(judgeInfo));
        
        boolean update = questionFeignClient.updateQuestionSubmitById(questionSubmitUpdate);
        if (!update) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "判题结果更新错误");
        }
    }

    /**
     * 批量判题
     */
    public void batchJudge(List<Long> questionSubmitIds) {
        log.info("开始批量判题，数量: {}", questionSubmitIds.size());
        
        for (Long questionSubmitId : questionSubmitIds) {
            try {
                doJudgeWithJudge0(questionSubmitId);
                log.info("题目 {} 判题完成", questionSubmitId);
            } catch (Exception e) {
                log.error("题目 {} 判题失败: {}", questionSubmitId, e.getMessage());
            }
        }
    }
} 