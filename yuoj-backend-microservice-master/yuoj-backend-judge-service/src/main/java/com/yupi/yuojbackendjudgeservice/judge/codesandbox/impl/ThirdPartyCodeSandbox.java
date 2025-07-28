package com.yupi.yuojbackendjudgeservice.judge.codesandbox.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yupi.yuojbackendjudgeservice.config.Judge0Config;
import com.yupi.yuojbackendjudgeservice.judge.codesandbox.CodeSandbox;
import com.yupi.yuojbackendmodel.model.codesandbox.ExecuteCodeRequest;
import com.yupi.yuojbackendmodel.model.codesandbox.ExecuteCodeResponse;
import com.yupi.yuojbackendmodel.model.codesandbox.JudgeInfo;
import com.yupi.yuojbackendmodel.model.codesandbox.judge0.Judge0ResultResponse;
import com.yupi.yuojbackendmodel.model.codesandbox.judge0.Judge0SubmissionRequest;
import com.yupi.yuojbackendmodel.model.codesandbox.judge0.Judge0SubmissionResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 第三方代码沙箱（调用Judge0 API）
 */
@Slf4j
@Component
public class ThirdPartyCodeSandbox implements CodeSandbox {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    private final Judge0Config judge0Config;
    
    @Autowired
    public ThirdPartyCodeSandbox(Judge0Config judge0Config) {
        this.judge0Config = judge0Config;
        this.objectMapper = new ObjectMapper();
        
        this.webClient = WebClient.builder()
                .baseUrl(judge0Config.getBaseUrl())
                .defaultHeader("X-RapidAPI-Key", judge0Config.getRapidapiKey())
                .defaultHeader("X-RapidAPI-Host", judge0Config.getRapidapiHost())
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        log.info("开始执行代码，语言：{}", executeCodeRequest.getLanguage());
        
        try {
            // 1. 提交代码到Judge0
            String token = submitCode(executeCodeRequest);
            if (token == null) {
                return createErrorResponse("代码提交失败");
            }
            
            // 2. 轮询获取执行结果
            Judge0ResultResponse result = pollResult(token);
            if (result == null) {
                return createErrorResponse("获取执行结果失败");
            }
            
            // 3. 处理执行结果
            return processResult(result, executeCodeRequest);
            
        } catch (Exception e) {
            log.error("代码执行异常", e);
            return createErrorResponse("代码执行异常：" + e.getMessage());
        }
    }
    
    /**
     * 提交代码到Judge0
     */
    private String submitCode(ExecuteCodeRequest request) {
        try {
            // 构建Judge0提交请求
            Judge0SubmissionRequest judge0Request = Judge0SubmissionRequest.builder()
                    .source_code(request.getCode())
                    .language_id(Judge0LanguageMapper.getLanguageId(request.getLanguage()))
                    .stdin(request.getInputList() != null && !request.getInputList().isEmpty() 
                            ? request.getInputList().get(0) : "")
                    .cpu_time_limit(5) // 5秒时间限制
                    .memory_limit(512000) // 512MB内存限制
                    .enable_network(false) // 禁用网络
                    .build();
            
            log.info("提交代码到Judge0，语言ID：{}", judge0Request.getLanguage_id());
            
            // 发送HTTP请求
            Judge0SubmissionResponse response = webClient.post()
                    .uri("/submissions")
                    .bodyValue(judge0Request)
                    .retrieve()
                    .bodyToMono(Judge0SubmissionResponse.class)
                    .block();
            
            if (response != null && response.getToken() != null) {
                log.info("代码提交成功，token：{}", response.getToken());
                return response.getToken();
            } else {
                log.error("代码提交失败，响应为空");
                return null;
            }
            
        } catch (Exception e) {
            log.error("提交代码异常", e);
            return null;
        }
    }
    
    /**
     * 轮询获取执行结果
     */
    private Judge0ResultResponse pollResult(String token) {
        int maxRetries = judge0Config.getMaxRetries();
        int retryCount = 0;
        
        while (retryCount < maxRetries) {
            try {
                Judge0ResultResponse result = webClient.get()
                        .uri("/submissions/{token}", token)
                        .retrieve()
                        .bodyToMono(Judge0ResultResponse.class)
                        .block();
                
                if (result != null) {
                    // 检查执行状态
                    if (result.getStatus_id() != null) {
                        // 状态ID 1-3 表示正在处理，4-6 表示已完成
                        if (result.getStatus_id() >= 4) {
                            log.info("代码执行完成，状态：{}", result.getStatus_description());
                            return result;
                        }
                    }
                }
                
                // 等待1秒后重试
                TimeUnit.SECONDS.sleep(1);
                retryCount++;
                
            } catch (Exception e) {
                log.error("轮询结果异常", e);
                retryCount++;
            }
        }
        
        log.error("轮询超时，token：{}", token);
        return null;
    }
    
    /**
     * 处理执行结果
     */
    private ExecuteCodeResponse processResult(Judge0ResultResponse result, ExecuteCodeRequest request) {
        List<String> outputList = new ArrayList<>();
        
        // 处理标准输出
        if (result.getStdout() != null && !result.getStdout().trim().isEmpty()) {
            outputList.add(result.getStdout().trim());
        }
        
        // 构建判题信息
        JudgeInfo judgeInfo = new JudgeInfo();
        judgeInfo.setMemory(result.getMemory() != null ? result.getMemory().longValue() : 0L);
        judgeInfo.setTime(result.getTime() != null ? result.getTime().longValue() : 0L);
        
        // 根据状态ID设置消息
        String message = "执行成功";
        Integer status = 0; // 成功状态
        
        if (result.getStatus_id() != null) {
            switch (result.getStatus_id()) {
                case 4: // Accepted
                    message = "执行成功";
                    status = 0;
                    break;
                case 5: // Wrong Answer
                    message = "答案错误";
                    status = 1;
                    break;
                case 6: // Compilation Error
                    message = "编译错误";
                    status = 2;
                    if (result.getCompile_output() != null) {
                        message += ": " + result.getCompile_output();
                    }
                    break;
                case 7: // Runtime Error
                    message = "运行时错误";
                    status = 3;
                    if (result.getStderr() != null) {
                        message += ": " + result.getStderr();
                    }
                    break;
                case 8: // Time Limit Exceeded
                    message = "时间超限";
                    status = 4;
                    break;
                case 9: // Memory Limit Exceeded
                    message = "内存超限";
                    status = 5;
                    break;
                default:
                    message = "未知错误";
                    status = 6;
                    break;
            }
        }
        
        // 如果有标准错误输出，添加到消息中
        if (result.getStderr() != null && !result.getStderr().trim().isEmpty()) {
            message += " - " + result.getStderr().trim();
        }
        
        judgeInfo.setMessage(message);
        
        return ExecuteCodeResponse.builder()
                .outputList(outputList)
                .message(message)
                .status(status)
                .judgeInfo(judgeInfo)
                .build();
    }
    
    /**
     * 创建错误响应
     */
    private ExecuteCodeResponse createErrorResponse(String errorMessage) {
        JudgeInfo judgeInfo = new JudgeInfo();
        judgeInfo.setMessage(errorMessage);
        judgeInfo.setMemory(0L);
        judgeInfo.setTime(0L);
        
        return ExecuteCodeResponse.builder()
                .outputList(new ArrayList<>())
                .message(errorMessage)
                .status(6) // 系统错误
                .judgeInfo(judgeInfo)
                .build();
    }
}
