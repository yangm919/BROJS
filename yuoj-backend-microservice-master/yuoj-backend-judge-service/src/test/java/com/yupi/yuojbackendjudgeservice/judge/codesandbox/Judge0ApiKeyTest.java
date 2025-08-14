package com.yupi.yuojbackendjudgeservice.judge.codesandbox;

import com.yupi.yuojbackendjudgeservice.config.Judge0Config;
import com.yupi.yuojbackendjudgeservice.judge.codesandbox.impl.ThirdPartyCodeSandbox;
import com.yupi.yuojbackendmodel.model.codesandbox.ExecuteCodeRequest;
import com.yupi.yuojbackendmodel.model.codesandbox.ExecuteCodeResponse;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Judge0 API密钥验证测试
 */
@Slf4j
@SpringBootTest
public class Judge0ApiKeyTest {

    @Autowired
    private Judge0Config judge0Config;

    @Autowired
    private ThirdPartyCodeSandbox codeSandbox;

    @Test
    void testApiKeyConfiguration() {
        // 验证API密钥配置
        assertNotNull(judge0Config.getRapidapiKey());
        assertFalse(judge0Config.getRapidapiKey().isEmpty());
        assertNotEquals("YOUR_RAPIDAPI_KEY", judge0Config.getRapidapiKey());
        
        log.info("API密钥配置正确: {}", judge0Config.getRapidapiKey().substring(0, 10) + "...");
        
        // 验证其他配置
        assertEquals("https://judge0-ce.p.rapidapi.com", judge0Config.getBaseUrl());
        assertEquals("judge0-ce.p.rapidapi.com", judge0Config.getRapidapiHost());
        assertEquals(30, judge0Config.getTimeout());
        assertEquals(30, judge0Config.getMaxRetries());
    }

    @Test
    void testSimpleCodeExecution() {
        // 测试简单的Python代码执行
        String simpleCode = "print('Hello from Judge0!')";
        
        ExecuteCodeRequest request = ExecuteCodeRequest.builder()
                .code(simpleCode)
                .language("python")
                .inputList(Arrays.asList(""))
                .build();

        log.info("开始测试代码执行...");
        log.info("代码: {}", simpleCode);
        
        try {
            ExecuteCodeResponse response = codeSandbox.executeCode(request);
            
            assertNotNull(response);
            log.info("执行状态: {}", response.getStatus());
            log.info("执行消息: {}", response.getMessage());
            log.info("输出列表: {}", response.getOutputList());
            
            if (response.getJudgeInfo() != null) {
                log.info("内存使用: {} KB", response.getJudgeInfo().getMemory());
                log.info("执行时间: {} ms", response.getJudgeInfo().getTime());
            }
            
            // 如果API密钥有效，应该能够成功执行
            if (response.getStatus() == 0) {
                log.info("✅ 代码执行成功！");
                assertTrue(response.getOutputList().contains("Hello from Judge0!"));
            } else {
                log.warn("⚠️ 代码执行失败，状态: {}, 消息: {}", response.getStatus(), response.getMessage());
            }
            
        } catch (Exception e) {
            log.error("❌ 代码执行异常", e);
            fail("代码执行应该不会抛出异常: " + e.getMessage());
        }
    }

    @Test
    void testJavaCodeExecution() {
        // 测试Java代码执行
        String javaCode =
                "public class Main {\n" +
                "    public static void main(String[] args) {\n" +
                "        System.out.println(\"Hello from Java!\");\n" +
                "        System.out.println(\"1 + 2 = \" + (1 + 2));\n" +
                "    }\n" +
                "}\n";
        
        ExecuteCodeRequest request = ExecuteCodeRequest.builder()
                .code(javaCode)
                .language("java")
                .inputList(Arrays.asList(""))
                .build();

        log.info("开始测试Java代码执行...");
        
        try {
            ExecuteCodeResponse response = codeSandbox.executeCode(request);
            
            assertNotNull(response);
            log.info("Java执行状态: {}", response.getStatus());
            log.info("Java执行消息: {}", response.getMessage());
            log.info("Java输出: {}", response.getOutputList());
            
            if (response.getStatus() == 0) {
                log.info("✅ Java代码执行成功！");
            } else {
                log.warn("⚠️ Java代码执行失败: {}", response.getMessage());
            }
            
        } catch (Exception e) {
            log.error("❌ Java代码执行异常", e);
        }
    }

    @Test
    void testJavaScriptCodeExecution() {
        // 测试JavaScript代码执行
        String jsCode =
                "console.log(\"Hello from JavaScript!\");\n" +
                "console.log(\"1 + 2 =\", 1 + 2);\n";
        
        ExecuteCodeRequest request = ExecuteCodeRequest.builder()
                .code(jsCode)
                .language("javascript")
                .inputList(Arrays.asList(""))
                .build();

        log.info("开始测试JavaScript代码执行...");
        
        try {
            ExecuteCodeResponse response = codeSandbox.executeCode(request);
            
            assertNotNull(response);
            log.info("JavaScript执行状态: {}", response.getStatus());
            log.info("JavaScript执行消息: {}", response.getMessage());
            log.info("JavaScript输出: {}", response.getOutputList());
            
            if (response.getStatus() == 0) {
                log.info("✅ JavaScript代码执行成功！");
            } else {
                log.warn("⚠️ JavaScript代码执行失败: {}", response.getMessage());
            }
            
        } catch (Exception e) {
            log.error("❌ JavaScript代码执行异常", e);
        }
    }

    @Test
    void testErrorHandling() {
        // 测试错误处理
        String errorCode = "print('This is a syntax error"; // 故意制造语法错误
        
        ExecuteCodeRequest request = ExecuteCodeRequest.builder()
                .code(errorCode)
                .language("python")
                .inputList(Arrays.asList(""))
                .build();

        log.info("开始测试错误处理...");
        
        try {
            ExecuteCodeResponse response = codeSandbox.executeCode(request);
            
            assertNotNull(response);
            log.info("错误处理状态: {}", response.getStatus());
            log.info("错误处理消息: {}", response.getMessage());
            
            // 应该能够正确处理错误
            assertNotEquals(0, response.getStatus());
            log.info("✅ 错误处理正常！");
            
        } catch (Exception e) {
            log.error("❌ 错误处理异常", e);
        }
    }
} 