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
 * Default judge strategy
 */
@Slf4j
public class DefaultJudgeStrategy implements JudgeStrategy {
    /**
     * Execute judging
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
        
        log.info("Starting judging - Input count: {}, Output count: {}, Test case count: {}", 
                inputList.size(), outputList.size(), judgeCaseList.size());
        
        // Check if there are output results
        if (outputList.isEmpty()) {
            log.warn("No output results");
            judgeInfoMessageEnum = JudgeInfoMessageEnum.WRONG_ANSWER;
            judgeInfoResponse.setMessage(judgeInfoMessageEnum.getValue());
            return judgeInfoResponse;
        }
        
        // Check if output count matches
        if (outputList.size() != judgeCaseList.size()) {
            log.warn("Output count mismatch - Expected: {}, Actual: {}", judgeCaseList.size(), outputList.size());
            judgeInfoMessageEnum = JudgeInfoMessageEnum.WRONG_ANSWER;
            judgeInfoResponse.setMessage(judgeInfoMessageEnum.getValue());
            return judgeInfoResponse;
        }
        
        // Process all test cases
        for (int i = 0; i < judgeCaseList.size(); i++) {
            JudgeCase judgeCase = judgeCaseList.get(i);
            String expectedOutput = judgeCase.getOutput().trim();
            String actualOutput = outputList.get(i).trim();
            
            log.info("Comparing test case {} - Expected: '{}', Actual: '{}'", i + 1, expectedOutput, actualOutput);
            
            // Compare output results
            if (!expectedOutput.equals(actualOutput)) {
                log.warn("Test case {} output mismatch - Expected: '{}', Actual: '{}'", i + 1, expectedOutput, actualOutput);
                judgeInfoMessageEnum = JudgeInfoMessageEnum.WRONG_ANSWER;
                judgeInfoResponse.setMessage(judgeInfoMessageEnum.getValue());
                return judgeInfoResponse;
            }
        }
        
        // Check question limits
        String judgeConfigStr = question.getJudgeConfig();
        JudgeConfig judgeConfig = JSONUtil.toBean(judgeConfigStr, JudgeConfig.class);
        Long needMemoryLimit = judgeConfig.getMemoryLimit();
        Long needTimeLimit = judgeConfig.getTimeLimit();
        if (memory > needMemoryLimit) {
            log.warn("Memory limit exceeded - Used: {}, Limit: {}", memory, needMemoryLimit);
            judgeInfoMessageEnum = JudgeInfoMessageEnum.MEMORY_LIMIT_EXCEEDED;
            judgeInfoResponse.setMessage(judgeInfoMessageEnum.getValue());
            return judgeInfoResponse;
        }
        if (time > needTimeLimit) {
            log.warn("Time limit exceeded - Used: {}, Limit: {}", time, needTimeLimit);
            judgeInfoMessageEnum = JudgeInfoMessageEnum.TIME_LIMIT_EXCEEDED;
            judgeInfoResponse.setMessage(judgeInfoMessageEnum.getValue());
            return judgeInfoResponse;
        }
        
        log.info("Judging successful");
        judgeInfoResponse.setMessage(judgeInfoMessageEnum.getValue());
        return judgeInfoResponse;
    }
}
