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
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.lang.Thread;

/**
 * Third-party code sandbox (calls Judge0 API)
 */
@Slf4j
@Component
public class Main implements CodeSandbox {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    private final Judge0Config judge0Config;
    
    @Autowired
    public Main(Judge0Config judge0Config) {
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
        log.info("Starting code execution, language: {}", executeCodeRequest.getLanguage());
        
        try {
            // Test Judge0 API connection
            if (!testJudge0Connection()) {
                log.error("Judge0 API connection failed");
                return createErrorResponse("Judge0 API connection failed");
            }
            
            // Process multiple test cases
            List<String> outputList = new ArrayList<>();
            List<String> inputList = executeCodeRequest.getInputList();
            
            if (inputList == null || inputList.isEmpty()) {
                return createErrorResponse("No test cases");
            }
            
            // Save the last test case result for extracting memory and time information
            Judge0ResultResponse lastResult = null;
            
            // Execute code for each test case
            for (int i = 0; i < inputList.size(); i++) {
                String input = inputList.get(i);
                log.info("Executing test case {}, input: {}", i + 1, input);
                
                // 1. Submit code to Judge0
                String token = submitCode(executeCodeRequest, input);
                if (token == null) {
                    return createErrorResponse("Code submission failed");
                }
                
                // 2. Poll for execution results
                Judge0ResultResponse result = pollResult(token);
                if (result == null) {
                    return createErrorResponse("Failed to get execution results");
                }
                
                // Save the last result
                lastResult = result;
                
                // 3. Process single test case result
                String output = processSingleResult(result);
                if (output != null) {
                    outputList.add(output);
                } else {
                    return createErrorResponse("Test case " + (i + 1) + " execution failed");
                }
                
                // 4. Add delay between test cases to avoid rate limiting
                if (i < inputList.size() - 1) {
                    try {
                        log.info("Waiting 1 second before executing next test case...");
                        Thread.sleep(1000L);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }
            
            // 4. Build final response using the last result to get memory and time information
            ExecuteCodeResponse finalResponse;
            if (lastResult != null) {
                finalResponse = processResult(lastResult, executeCodeRequest);
                // Update the output list with all test case results
                finalResponse.setOutputList(outputList);
            } else {
                finalResponse = createErrorResponse("No execution results");
            }
            
            return finalResponse;
            
        } catch (Exception e) {
            log.error("Code execution exception", e);
            return createErrorResponse("Code execution exception: " + e.getMessage());
        }
    }
    
    /**
     * Test Judge0 API connection
     */
    private boolean testJudge0Connection() {
        try {
            log.info("Testing Judge0 API connection...");
            
            // Try to get language list to test connection
            String response = webClient.get()
                    .uri("/languages")
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            
            log.info("Judge0 API connection test response: {}", response != null ? response.substring(0, Math.min(200, response.length())) + "..." : "null");
            
            return response != null && !response.contains("error");
            
        } catch (Exception e) {
            log.error("Judge0 API connection test failed", e);
            return false;
        }
    }
    
    /**
     * Submit code to Judge0
     */
    private String submitCode(ExecuteCodeRequest request, String input) {
        int maxRetries = 3;
        int retryCount = 0;
        
        while (retryCount < maxRetries) {
            try {
                // Build Judge0 submission request
                Judge0SubmissionRequest judge0Request = Judge0SubmissionRequest.builder()
                        .source_code(request.getCode())
                        .language_id(Judge0LanguageMapper.getLanguageId(request.getLanguage()))
                        .stdin(input)
                        .cpu_time_limit(5) // 5 second time limit
                        .memory_limit(256000) // 256MB memory limit
                        .enable_network(false) // Disable network
                        .build();
                
                log.info("Submitting code to Judge0, language ID: {}, input: {}, retry count: {}", 
                        judge0Request.getLanguage_id(), judge0Request.getStdin(), retryCount + 1);
                
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
                
            } catch (WebClientResponseException.TooManyRequests e) {
                log.warn("Judge0 API限流，等待重试... (重试 {}/{})", retryCount + 1, maxRetries);
                retryCount++;
                
                if (retryCount < maxRetries) {
                    try {
                        // 指数退避：等待时间递增
                        int waitTime = retryCount * 2; // 2秒, 4秒, 6秒
                        log.info("等待{}秒后重试...", waitTime);
                        Thread.sleep(waitTime * 1000L);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                } else {
                    log.error("Judge0 API限流，已达到最大重试次数");
                    return null;
                }
            } catch (Exception e) {
                log.error("提交代码异常", e);
                return null;
            }
        }
        
        return null;
    }
    
    /**
     * 轮询获取执行结果
     */
    private Judge0ResultResponse pollResult(String token) {
        int maxRetries = judge0Config.getMaxRetries();
        int retryCount = 0;
        
        log.info("开始轮询Judge0结果，token: {}, 最大重试次数: {}", token, maxRetries);
        
        while (retryCount < maxRetries) {
            try {
                log.info("第{}次轮询Judge0结果", retryCount + 1);
                
                // 先获取原始响应字符串
                String rawResponse = webClient.get()
                        .uri("/submissions/{token}", token)
                        .retrieve()
                        .bodyToMono(String.class)
                        .block();
                
                log.info("Judge0原始响应: {}", rawResponse);
                
                // 然后解析为对象
                Judge0ResultResponse result = webClient.get()
                        .uri("/submissions/{token}", token)
                        .retrieve()
                        .bodyToMono(Judge0ResultResponse.class)
                        .block();
                
                if (result != null) {
                    log.info("获取到Judge0响应 - 状态: {}", result.getStatus());
                    
                    // 检查执行状态
                    if (result.getStatus() != null && result.getStatus().getId() != null) {
                        // 状态ID 1-2 表示正在处理，3-9 表示已完成
                        if (result.getStatus().getId() >= 3) {
                            log.info("代码执行完成，状态：{}", result.getStatus().getDescription());
                            // 检查是否包含内存和时间信息
                            if (result.getMemory() != null && result.getTime() != null) {
                                log.info("获取到完整结果，内存：{}，时间：{}", result.getMemory(), result.getTime());
                                return result;
                            } else {
                                log.info("结果不完整，内存：{}，时间：{}，继续等待...", result.getMemory(), result.getTime());
                            }
                        } else {
                            log.info("代码正在处理中，状态：{}", result.getStatus().getDescription());
                        }
                    } else {
                        log.warn("Judge0响应中状态为空");
                    }
                } else {
                    log.warn("Judge0响应为空");
                }
                
                // 等待2秒后重试
                TimeUnit.SECONDS.sleep(2);
                retryCount++;
                
            } catch (Exception e) {
                log.error("第{}次轮询结果异常", retryCount + 1, e);
                retryCount++;
                // 异常时等待更长时间
                try {
                    TimeUnit.SECONDS.sleep(3);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
        
        log.error("轮询超时，token：{}，已重试{}次", token, maxRetries);
        return null;
    }
    
    /**
     * 处理单个Judge0执行结果
     */
    private String processSingleResult(Judge0ResultResponse result) {
        log.info("处理单个Judge0结果 - 状态: {}", result.getStatus());
        log.info("标准输出: '{}'", result.getStdout());
        log.info("标准错误: '{}'", result.getStderr());
        log.info("编译输出: '{}'", result.getCompile_output());
        
        // 检查执行状态
        if (result.getStatus() != null && result.getStatus().getId() != null) {
            switch (result.getStatus().getId()) {
                case 3: // Accepted
                    log.info("Judge0状态: Accepted");
                    break;
                case 4: // Wrong Answer
                    log.error("Judge0状态: Wrong Answer");
                    return null;
                case 5: // Compilation Error
                    log.error("Judge0状态: Compilation Error");
                    return null;
                case 6: // Runtime Error
                    log.error("Judge0状态: Runtime Error");
                    return null;
                case 7: // Time Limit Exceeded
                    log.warn("Judge0状态: Time Limit Exceeded");
                    return null;
                case 8: // Memory Limit Exceeded
                    log.warn("Judge0状态: Memory Limit Exceeded");
                    return null;
                default:
                    log.error("Judge0状态: 未知错误 - {}", result.getStatus().getId());
                    return null;
            }
        }
        
        // 处理标准输出
        if (result.getStdout() != null && !result.getStdout().trim().isEmpty()) {
            String trimmedOutput = result.getStdout().trim();
            log.info("返回输出: '{}'", trimmedOutput);
            return trimmedOutput;
        } else {
            log.warn("标准输出为空或null");
            return null;
        }
    }
    
    /**
     * 处理Judge0执行结果（保留原方法用于兼容）
     */
    private ExecuteCodeResponse processResult(Judge0ResultResponse result, ExecuteCodeRequest request) {
        List<String> outputList = new ArrayList<>();
        
        log.info("处理Judge0结果 - 状态: {}", result.getStatus());
        log.info("标准输出: '{}'", result.getStdout());
        log.info("标准错误: '{}'", result.getStderr());
        log.info("编译输出: '{}'", result.getCompile_output());
        
        // 处理标准输出
        if (result.getStdout() != null && !result.getStdout().trim().isEmpty()) {
            String trimmedOutput = result.getStdout().trim();
            outputList.add(trimmedOutput);
            log.info("添加输出到列表: '{}'", trimmedOutput);
        } else {
            log.warn("标准输出为空或null");
        }
        
        // 构建判题信息
        JudgeInfo judgeInfo = new JudgeInfo();
        judgeInfo.setMemory(result.getMemory() != null ? result.getMemory().longValue() : 0L);
        
        // 处理时间字段
        if (result.getTime() != null) {
            try {
                judgeInfo.setTime((long)(Double.parseDouble(result.getTime()) * 1000)); // 转换为毫秒
            } catch (NumberFormatException e) {
                log.warn("无法解析时间字段: {}", result.getTime());
                judgeInfo.setTime(0L);
            }
        } else {
            judgeInfo.setTime(0L);
        }
        
        // 根据状态ID设置消息
        String message = "执行成功";
        Integer status = 0; // 成功状态
        
        if (result.getStatus() != null && result.getStatus().getId() != null) {
            switch (result.getStatus().getId()) {
                case 3: // Accepted
                    message = "执行成功";
                    status = 0;
                    log.info("Judge0状态: Accepted");
                    break;
                case 4: // Wrong Answer
                    message = "答案错误";
                    status = 1;
                    log.info("Judge0状态: Wrong Answer");
                    break;
                case 5: // Compilation Error
                    message = "编译错误";
                    status = 2;
                    if (result.getCompile_output() != null) {
                        message += ": " + result.getCompile_output();
                    }
                    log.error("Judge0状态: Compilation Error - {}", message);
                    break;
                case 6: // Runtime Error
                    message = "运行时错误";
                    status = 3;
                    if (result.getStderr() != null) {
                        message += ": " + result.getStderr();
                    }
                    log.error("Judge0状态: Runtime Error - {}", message);
                    break;
                case 7: // Time Limit Exceeded
                    message = "时间超限";
                    status = 4;
                    log.warn("Judge0状态: Time Limit Exceeded");
                    break;
                case 8: // Memory Limit Exceeded
                    message = "内存超限";
                    status = 5;
                    log.warn("Judge0状态: Memory Limit Exceeded");
                    break;
                default:
                    message = "未知错误";
                    status = 6;
                    log.error("Judge0状态: 未知错误 - {}", result.getStatus().getId());
                    break;
            }
        }
        
        // 如果有标准错误输出，添加到消息中
        if (result.getStderr() != null && !result.getStderr().trim().isEmpty()) {
            message += " - " + result.getStderr().trim();
        }
        
        judgeInfo.setMessage(message);
        
        log.info("最终输出列表大小: {}", outputList.size());
        log.info("最终消息: {}", message);
        
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
