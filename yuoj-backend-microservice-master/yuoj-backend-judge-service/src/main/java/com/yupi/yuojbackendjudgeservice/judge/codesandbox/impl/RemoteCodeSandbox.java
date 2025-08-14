package com.yupi.yuojbackendjudgeservice.judge.codesandbox.impl;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yupi.yuojbackendcommon.common.ErrorCode;
import com.yupi.yuojbackendcommon.exception.BusinessException;
import com.yupi.yuojbackendjudgeservice.config.Judge0Config;
import com.yupi.yuojbackendjudgeservice.judge.codesandbox.CodeSandbox;
import com.yupi.yuojbackendmodel.model.codesandbox.ExecuteCodeRequest;
import com.yupi.yuojbackendmodel.model.codesandbox.ExecuteCodeResponse;
import com.yupi.yuojbackendmodel.model.codesandbox.JudgeInfo;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Resource;
import java.io.IOException;

import static java.lang.Thread.sleep;

/**
 * 远程代码沙箱（实际调用接口的沙箱）
 */
public class RemoteCodeSandbox implements CodeSandbox {
    @Resource
    private Judge0Config judge0Config;

    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        System.out.println("Remote");
        String token;
        try {
            token = createSubmission(executeCodeRequest);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        String responseBody = getSubmission(token);
      	System.out.println("responseBody: " + responseBody);
        if (StringUtils.isBlank(responseBody)) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "Failed to get submission response");
        }
        ExecuteCodeResponse executeCodeResponse = new ExecuteCodeResponse();
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode jsonNode = objectMapper.readTree(responseBody);
          	System.out.println("jsonNode: " + jsonNode.toString());
            executeCodeResponse.setMessage(jsonNode.get("message").asText());
            executeCodeResponse.setStatus(jsonNode.get("status").get("id").asInt());
            JudgeInfo judgeInfo = new JudgeInfo();
            judgeInfo.setTime(jsonNode.get("time").asLong());
            judgeInfo.setMemory(jsonNode.get("memory").asLong());
            judgeInfo.setMessage(jsonNode.get("status").get("description").asText());
            executeCodeResponse.setJudgeInfo(judgeInfo);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return executeCodeResponse;
    }

    private String createSubmission(ExecuteCodeRequest executeCodeRequest) throws IOException {
        // 请求URL
        String url = "http://47.117.87.178:2358/submissions";
        // 构建请求体JSON
        JSONObject json = new JSONObject();
        json.set("source_code", executeCodeRequest.getCode());
        json.set("language_id", getLanguageId(executeCodeRequest.getLanguage()));

        System.out.println("json body: " + json.toString());
        // 发送POST请求
        HttpResponse response = HttpRequest.post(url)
                .body(json.toString())
                .contentType("application/json")
                .execute();

        // 输出响应结果
        System.out.println("Status: " + response.getStatus());
        System.out.println("Response: " + response.body());

        // 使用 Jackson 解析 JSON 响应
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(response.body());
        // 返回 token 字段
        return rootNode.get("token").asText();
    }

    private String getSubmission(String token) {
        // 请求URL
        String url = "http://47.117.87.178:2358/submissions/"+ token;
        // 发送GET请求
        HttpResponse response = HttpRequest.get(url)
                .execute();

        // 输出响应结果
        System.out.println("Status: " + response.getStatus());
        System.out.println("Response: " + response.body());
        return response.body();
    }

    private String getLanguageId(String language) {
        System.out.println("choose language: " + language);
        if (StringUtils.isBlank(language)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "Language cannot be empty");
        }
        switch (language) {
            case "c":
                return "50";
            case "cpp":
                return "54";
            case "java":
                return "62";
            case "python":
                return "71";
            case "javascript":
                return "63";
            case "go":
                return "60";
            // 添加其他语言的映射
            default:
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "Unsupported language: " + language);
        }
    }
}
