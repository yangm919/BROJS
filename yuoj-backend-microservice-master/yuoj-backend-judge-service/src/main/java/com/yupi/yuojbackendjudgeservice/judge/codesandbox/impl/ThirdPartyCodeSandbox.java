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
            // 测试Judge0 API连接
            if (!testJudge0Connection()) {
                log.error("Judge0 API连接失败");
                return createErrorResponse("Judge0 API连接失败");
            }
            
            // 处理多个测试用例
            List<String> outputList = new ArrayList<>();
            List<String> inputList = executeCodeRequest.getInputList();
            
            if (inputList == null || inputList.isEmpty()) {
                return createErrorResponse("没有测试用例");
            }
            
            // 为每个测试用例执行代码
            for (int i = 0; i < inputList.size(); i++) {
                String input = inputList.get(i);
                log.info("执行第{}个测试用例，输入：{}", i + 1, input);
                
                // 1. 提交代码到Judge0
                String token = submitCode(executeCodeRequest, input);
                if (token == null) {
                    return createErrorResponse("代码提交失败");
                }
                
                // 2. 轮询获取执行结果
                Judge0ResultResponse result = pollResult(token);
                if (result == null) {
                    return createErrorResponse("获取执行结果失败");
                }
                
                // 3. 处理单个测试用例的结果
                String output = processSingleResult(result);
                if (output != null) {
                    outputList.add(output);
                } else {
                    return createErrorResponse("第" + (i + 1) + "个测试用例执行失败");
                }
                
                // 4. 在测试用例之间添加延迟，避免触发限流
                if (i < inputList.size() - 1) {
                    try {
                        log.info("等待1秒后执行下一个测试用例...");
                        Thread.sleep(1000L);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }
            
            // 4. 构建最终响应
            return ExecuteCodeResponse.builder()
                    .outputList(outputList)
                    .message("执行成功")
                    .status(0)
                    .judgeInfo(new JudgeInfo())
                    .build();
            
        } catch (Exception e) {
            log.error("代码执行异常", e);
            return createErrorResponse("代码执行异常：" + e.getMessage());
        }
    }
    
    /**
     * 测试Judge0 API连接
     */
    private boolean testJudge0Connection() {
        try {
            log.info("测试Judge0 API连接...");
            
            // 尝试获取语言列表来测试连接
            String response = webClient.get()
                    .uri("/languages")
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            
            log.info("Judge0 API连接测试响应: {}", response != null ? response.substring(0, Math.min(200, response.length())) + "..." : "null");
            
            return response != null && !response.contains("error");
            
        } catch (Exception e) {
            log.error("Judge0 API连接测试失败", e);
            return false;
        }
    }
    
    /**
     * 提交代码到Judge0
     */
    private String submitCode(ExecuteCodeRequest request, String input) {
        int maxRetries = 3;
        int retryCount = 0;
        
        while (retryCount < maxRetries) {
            try {
                // 构建Judge0提交请求
                Judge0SubmissionRequest judge0Request = Judge0SubmissionRequest.builder()
                        .source_code(request.getCode())
                        .language_id(Judge0LanguageMapper.getLanguageId(request.getLanguage()))
                        .stdin(input)
                        .cpu_time_limit(5) // 5秒时间限制
                        .memory_limit(256000) // 256MB内存限制
                        .enable_network(false) // 禁用网络
                        .build();
                
                log.info("提交代码到Judge0，语言ID：{}，输入：{}，重试次数：{}", 
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
                            return result;
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
