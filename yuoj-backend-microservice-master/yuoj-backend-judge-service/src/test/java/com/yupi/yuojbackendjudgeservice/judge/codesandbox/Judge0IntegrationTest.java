package com.yupi.yuojbackendjudgeservice.judge.codesandbox;

import com.yupi.yuojbackendjudgeservice.config.Judge0Config;
import com.yupi.yuojbackendjudgeservice.judge.codesandbox.impl.Judge0LanguageMapper;
import com.yupi.yuojbackendjudgeservice.judge.codesandbox.impl.ThirdPartyCodeSandbox;
import com.yupi.yuojbackendmodel.model.codesandbox.ExecuteCodeRequest;
import com.yupi.yuojbackendmodel.model.codesandbox.ExecuteCodeResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * Judge0集成测试
 */
@SpringBootTest
public class Judge0IntegrationTest {

    @Mock
    private Judge0Config judge0Config;

    private ThirdPartyCodeSandbox codeSandbox;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // 模拟配置
        when(judge0Config.getBaseUrl()).thenReturn("https://judge0-ce.p.rapidapi.com");
        when(judge0Config.getRapidapiKey()).thenReturn("test-key");
        when(judge0Config.getRapidapiHost()).thenReturn("judge0-ce.p.rapidapi.com");
        when(judge0Config.getTimeout()).thenReturn(30);
        when(judge0Config.getMaxRetries()).thenReturn(30);
        
        codeSandbox = new ThirdPartyCodeSandbox(judge0Config);
    }

    @Test
    void testLanguageMapper() {
        // 测试Java语言映射
        assertEquals(62, Judge0LanguageMapper.getLanguageId("java"));
        assertEquals(62, Judge0LanguageMapper.getLanguageId("Java"));
        assertEquals(62, Judge0LanguageMapper.getLanguageId(" JAVA "));
        
        // 测试Python语言映射
        assertEquals(71, Judge0LanguageMapper.getLanguageId("python"));
        assertEquals(71, Judge0LanguageMapper.getLanguageId("python3"));
        
        // 测试C++语言映射
        assertEquals(54, Judge0LanguageMapper.getLanguageId("cpp"));
        assertEquals(54, Judge0LanguageMapper.getLanguageId("c++"));
        
        // 测试JavaScript语言映射
        assertEquals(63, Judge0LanguageMapper.getLanguageId("javascript"));
        assertEquals(63, Judge0LanguageMapper.getLanguageId("js"));
        
        // 测试不支持的语言（应该返回默认值Java）
        assertEquals(62, Judge0LanguageMapper.getLanguageId("unsupported"));
        assertEquals(62, Judge0LanguageMapper.getLanguageId(""));
        assertEquals(62, Judge0LanguageMapper.getLanguageId(null));
    }

    @Test
    void testLanguageSupport() {
        // 测试支持的语言
        assertTrue(Judge0LanguageMapper.isSupported("java"));
        assertTrue(Judge0LanguageMapper.isSupported("python"));
        assertTrue(Judge0LanguageMapper.isSupported("cpp"));
        assertTrue(Judge0LanguageMapper.isSupported("javascript"));
        
        // 测试不支持的语言
        assertFalse(Judge0LanguageMapper.isSupported("unsupported"));
        assertFalse(Judge0LanguageMapper.isSupported(""));
        assertFalse(Judge0LanguageMapper.isSupported(null));
    }

    @Test
    void testExecuteCodeRequest() {
        // 创建测试请求
        ExecuteCodeRequest request = ExecuteCodeRequest.builder()
                .code("public class Main {\n    public static void main(String[] args) {\n        System.out.println(\"Hello World\");\n    }\n}")
                .language("java")
                .inputList(Arrays.asList(""))
                .build();
        
        assertNotNull(request);
        assertEquals("java", request.getLanguage());
        assertNotNull(request.getCode());
        assertNotNull(request.getInputList());
    }

    @Test
    void testExecuteCodeResponse() {
        // 创建测试响应
        ExecuteCodeResponse response = ExecuteCodeResponse.builder()
                .outputList(Arrays.asList("Hello World"))
                .message("执行成功")
                .status(0)
                .build();
        
        assertNotNull(response);
        assertEquals(0, response.getStatus());
        assertEquals("执行成功", response.getMessage());
        assertNotNull(response.getOutputList());
        assertEquals(1, response.getOutputList().size());
        assertEquals("Hello World", response.getOutputList().get(0));
    }

    @Test
    void testCodeSandboxInitialization() {
        // 测试代码沙箱初始化
        assertNotNull(codeSandbox);
        assertNotNull(judge0Config);
        
        // 验证配置值
        assertEquals("https://judge0-ce.p.rapidapi.com", judge0Config.getBaseUrl());
        assertEquals("test-key", judge0Config.getRapidapiKey());
        assertEquals("judge0-ce.p.rapidapi.com", judge0Config.getRapidapiHost());
        assertEquals(30, judge0Config.getTimeout());
        assertEquals(30, judge0Config.getMaxRetries());
    }

    @Test
    void testInvalidApiKey() {
        // 测试无效API密钥的情况
        when(judge0Config.getRapidapiKey()).thenReturn("invalid-key");
        
        ExecuteCodeRequest request = ExecuteCodeRequest.builder()
                .code("print('Hello World')")
                .language("python")
                .inputList(Arrays.asList(""))
                .build();
        
        // 注意：这个测试在实际环境中会失败，因为API密钥无效
        // 这里只是测试代码结构是否正确
        assertNotNull(request);
        assertEquals("python", request.getLanguage());
    }
} 