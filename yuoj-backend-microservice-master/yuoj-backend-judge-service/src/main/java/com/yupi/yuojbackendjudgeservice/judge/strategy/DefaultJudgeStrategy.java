package com.yupi.yuojbackendjudgeservice.judge.strategy;
import cn.hutool.json.JSONUtil;
import com.yupi.yuojbackendmodel.model.codesandbox.JudgeInfo;
import com.yupi.yuojbackendmodel.model.dto.question.JudgeCase;
import com.yupi.yuojbackendmodel.model.dto.question.JudgeConfig;
import com.yupi.yuojbackendmodel.model.entity.Question;
import com.yupi.yuojbackendmodel.model.enums.JudgeInfoMessageEnum;
import lombok.extern.slf4j.Slf4j;
import java.util.List;
/**
 * 默认判题策略
 */
@Slf4j
public class DefaultJudgeStrategy implements JudgeStrategy {
    /**
     * 执行判题
     * @param judgeContext
     * @return
     */
    @Override
    public JudgeInfo doJudge(JudgeContext judgeContext) {
        JudgeInfo judgeInfo = judgeContext.getJudgeInfo();
        Long memory = judgeInfo.getMemory();
        Long time = judgeInfo.getTime();
        List<String> inputList = judgeContext.getInputList();
        List<String> outputList = judgeContext.getOutputList();
        Question question = judgeContext.getQuestion();
        List<JudgeCase> judgeCaseList = judgeContext.getJudgeCaseList();
        JudgeInfoMessageEnum judgeInfoMessageEnum = JudgeInfoMessageEnum.ACCEPTED;
        JudgeInfo judgeInfoResponse = new JudgeInfo();
        judgeInfoResponse.setMemory(memory);
        judgeInfoResponse.setTime(time);
        
        log.info("开始判题 - 输入数量: {}, 输出数量: {}, 测试用例数量: {}", 
                inputList.size(), outputList.size(), judgeCaseList.size());
        
        // 检查是否有输出结果
        if (outputList.isEmpty()) {
            log.warn("没有输出结果");
            judgeInfoMessageEnum = JudgeInfoMessageEnum.WRONG_ANSWER;
            judgeInfoResponse.setMessage(judgeInfoMessageEnum.getValue());
            return judgeInfoResponse;
        }
        
        // 对于Judge0，我们只处理第一个测试用例
        if (!judgeCaseList.isEmpty() && !outputList.isEmpty()) {
            JudgeCase judgeCase = judgeCaseList.get(0);
            String expectedOutput = judgeCase.getOutput().trim();
            String actualOutput = outputList.get(0).trim();
            
            log.info("比较输出 - 期望: '{}', 实际: '{}'", expectedOutput, actualOutput);
            
            // 比较输出结果
            if (!expectedOutput.equals(actualOutput)) {
                log.warn("输出不匹配 - 期望: '{}', 实际: '{}'", expectedOutput, actualOutput);
                judgeInfoMessageEnum = JudgeInfoMessageEnum.WRONG_ANSWER;
                judgeInfoResponse.setMessage(judgeInfoMessageEnum.getValue());
                return judgeInfoResponse;
            }
        }
        
        // 判断题目限制
        String judgeConfigStr = question.getJudgeConfig();
        JudgeConfig judgeConfig = JSONUtil.toBean(judgeConfigStr, JudgeConfig.class);
        Long needMemoryLimit = judgeConfig.getMemoryLimit();
        Long needTimeLimit = judgeConfig.getTimeLimit();
        if (memory > needMemoryLimit) {
            log.warn("内存超限 - 使用: {}, 限制: {}", memory, needMemoryLimit);
            judgeInfoMessageEnum = JudgeInfoMessageEnum.MEMORY_LIMIT_EXCEEDED;
            judgeInfoResponse.setMessage(judgeInfoMessageEnum.getValue());
            return judgeInfoResponse;
        }
        if (time > needTimeLimit) {
            log.warn("时间超限 - 使用: {}, 限制: {}", time, needTimeLimit);
            judgeInfoMessageEnum = JudgeInfoMessageEnum.TIME_LIMIT_EXCEEDED;
            judgeInfoResponse.setMessage(judgeInfoMessageEnum.getValue());
            return judgeInfoResponse;
        }
        
        log.info("判题成功");
        judgeInfoResponse.setMessage(judgeInfoMessageEnum.getValue());
        return judgeInfoResponse;
    }
}
