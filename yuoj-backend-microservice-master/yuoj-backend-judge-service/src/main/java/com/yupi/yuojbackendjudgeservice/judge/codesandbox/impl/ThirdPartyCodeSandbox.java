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
        log.info("=== ThirdPartyCodeSandbox.executeCode called ===");
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
          	System.out.println("inputList:" + executeCodeRequest.getInputList());
            
            if (inputList == null || inputList.isEmpty()) {
                return createErrorResponse("No test cases");
            }
            
            // Save the last test case result for extracting memory and time information
            Judge0ResultResponse lastResult = null;
            
            // Execute code for each test case
            for (int i = 0; i < inputList.size(); i++) {
                String input = inputList.get(i);
              	System.out.println("input:" + input);
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
                log.info("Processing last result with memory: {}, time: {}", lastResult.getMemory(), lastResult.getTime());
                finalResponse = processResult(lastResult, executeCodeRequest);
                // Update the output list with all test case results
                finalResponse.setOutputList(outputList);
                log.info("Final response before return - judgeInfo: {}", finalResponse.getJudgeInfo());
            } else {
                finalResponse = createErrorResponse("No execution results");
            }
            
            log.info("ThirdPartyCodeSandbox.executeCode returning final response: {}", finalResponse);
            return finalResponse;
            
        } catch (Exception e) {
            log.error("Code execution exception", e);
            return createErrorResponse("Code execution failed: " + e.getMessage());
        }
    }

    /**
     * Test Judge0 API connection
     */
    private boolean testJudge0Connection() {
        try {
            String response = webClient.get()
                    .uri("/languages")
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            return response != null && !response.isEmpty();
        } catch (Exception e) {
            log.error("Judge0 API connection test failed", e);
            return false;
        }
    }

    /**
     * Submit code to Judge0
     */
    private String submitCode(ExecuteCodeRequest request, String input) {
        try {
            // Get language ID
            Integer languageId = Judge0LanguageMapper.getLanguageId(request.getLanguage());
            
            // Create submission request
            Judge0SubmissionRequest submissionRequest = new Judge0SubmissionRequest();
            submissionRequest.setLanguage_id(languageId);
            submissionRequest.setSource_code(request.getCode());
            submissionRequest.setStdin(input);
            
            log.info("Submitting code to Judge0, language ID: {}, input: {}, retry count: {}", 
                    languageId, input, 1);
            
            // Submit to Judge0
            Judge0SubmissionResponse response = webClient.post()
                    .uri("/submissions")
                    .body(Mono.just(submissionRequest), Judge0SubmissionRequest.class)
                    .retrieve()
                    .bodyToMono(Judge0SubmissionResponse.class)
                    .block();
            
            if (response != null && response.getToken() != null) {
                log.info("Code submission successful, token: {}", response.getToken());
                return response.getToken();
            } else {
                log.error("Code submission failed, response: {}", response);
                return null;
            }
            
        } catch (Exception e) {
            log.error("Code submission exception", e);
            return null;
        }
    }

    /**
     * Poll for execution results
     */
    private Judge0ResultResponse pollResult(String token) {
        try {
            log.info("Starting to poll Judge0 results, token: {}, max retries: {}", 
                    token, judge0Config.getMaxRetries());
            
            for (int i = 1; i <= judge0Config.getMaxRetries(); i++) {
                log.info("Polling Judge0 results, attempt {}", i);
                
                Judge0ResultResponse result = webClient.get()
                        .uri("/submissions/" + token)
                        .retrieve()
                        .bodyToMono(Judge0ResultResponse.class)
                        .block();
                
                if (result != null && result.getStatus() != null) {
                    log.info("Received Judge0 response - status: {}", result.getStatus());
                    
                    // Check if processing is complete
                    if (result.getStatus().getId() == 3) { // Accepted
                        log.info("Code execution completed, status: Accepted");
                        return result;
                    } else if (result.getStatus().getId() == 4) { // Wrong Answer
                        log.info("Code execution completed, status: Wrong Answer");
                        return result;
                    } else if (result.getStatus().getId() == 5) { // Time Limit Exceeded
                        log.info("Code execution completed, status: Time Limit Exceeded");
                        return result;
                    } else if (result.getStatus().getId() == 6) { // Compilation Error
                        log.info("Code execution completed, status: Compilation Error");
                        return result;
                    } else if (result.getStatus().getId() == 1 || result.getStatus().getId() == 2) {
                        // Still processing
                        log.info("Code is still processing, status: {}", result.getStatus().getDescription());
                        try {
                            Thread.sleep(2000L); // Wait 2 seconds before next poll
                        } catch (InterruptedException ie) {
                            Thread.currentThread().interrupt();
                            break;
                        }
                    } else {
                        log.error("Unknown status: {}", result.getStatus());
                        return result;
                    }
                } else {
                    log.error("Invalid response from Judge0");
                    return null;
                }
            }
            
            log.error("Max retries reached, polling failed");
            return null;
            
        } catch (Exception e) {
            log.error("Polling exception", e);
            return null;
        }
    }

    /**
     * Process single test case result
     */
    private String processSingleResult(Judge0ResultResponse result) {
        try {
            log.info("Processing single Judge0 result - status: {}", result.getStatus());
            
            // Log detailed information
            log.info("Standard output: '{}'", result.getStdout());
            log.info("Standard error: '{}'", result.getStderr());
            log.info("Compilation output: '{}'", result.getCompile_output());
            log.info("Judge0 status: {}", result.getStatus().getDescription());
            
            // Check if execution was successful
            if (result.getStatus().getId() == 3) { // Accepted
                String output = result.getStdout();
                if (output != null) {
                    output = output.trim();
                    log.info("Returned output: '{}'", output);
                    return output;
                } else {
                    log.error("No output for accepted submission");
                    return null;
                }
            } else {
                log.error("Execution failed with status: {}", result.getStatus().getDescription());
                return null;
            }
            
        } catch (Exception e) {
            log.error("Error processing single result", e);
            return null;
        }
    }

    /**
     * Process result and create final response with memory and time information
     */
    private ExecuteCodeResponse processResult(Judge0ResultResponse result, ExecuteCodeRequest request) {
        try {
            log.info("Processing final result with memory and time information");
            log.info("Judge0ResultResponse - memory: {}, time: {}, status: {}", 
                    result.getMemory(), result.getTime(), result.getStatus());
            
            // 添加更详细的调试信息
            log.info("Raw Judge0ResultResponse object: {}", result);
            log.info("Memory field type: {}, value: {}", 
                    result.getMemory() != null ? result.getMemory().getClass().getSimpleName() : "null", 
                    result.getMemory());
            log.info("Time field type: {}, value: {}", 
                    result.getTime() != null ? result.getTime().getClass().getSimpleName() : "null", 
                    result.getTime());
            
            ExecuteCodeResponse response = new ExecuteCodeResponse();
            response.setStatus(0); // Success
            response.setMessage("Execution successful");
            
            // Create JudgeInfo with memory and time
            JudgeInfo judgeInfo = new JudgeInfo();
            judgeInfo.setMessage("Execution successful");
            
            // Extract memory information
            if (result.getMemory() != null) {
                judgeInfo.setMemory(result.getMemory().longValue());
                log.info("Memory usage: {} KB", result.getMemory());
            } else {
                judgeInfo.setMemory(0L);
                log.warn("Memory information not available");
            }
            
            // Extract time information
            if (result.getTime() != null) {
                try {
                    double timeSeconds = Double.parseDouble(result.getTime());
                    long timeMillis = (long)(timeSeconds * 1000); // Convert to milliseconds
                    judgeInfo.setTime(timeMillis);
                    log.info("Execution time: {} seconds ({} ms)", timeSeconds, timeMillis);
                } catch (NumberFormatException e) {
                    log.warn("Unable to parse time field: {}", result.getTime());
                    judgeInfo.setTime(0L);
                }
            } else {
                judgeInfo.setTime(0L);
                log.warn("Time information not available");
            }
            
            response.setJudgeInfo(judgeInfo);
            
            log.info("Final response created with judgeInfo: {}", judgeInfo);
            return response;
            
        } catch (Exception e) {
            log.error("Error processing final result", e);
            return createErrorResponse("Error processing result: " + e.getMessage());
        }
    }

    /**
     * Create error response
     */
    private ExecuteCodeResponse createErrorResponse(String errorMessage) {
        log.error("Creating error response: {}", errorMessage);
        
        ExecuteCodeResponse response = new ExecuteCodeResponse();
        response.setStatus(1); // Error
        response.setMessage(errorMessage);
        
        JudgeInfo judgeInfo = new JudgeInfo();
        judgeInfo.setMessage("Execution failed");
        judgeInfo.setMemory(0L);
        judgeInfo.setTime(0L);
        response.setJudgeInfo(judgeInfo);
        
        return response;
    }
} 