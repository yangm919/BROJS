package com.yupi.yuojbackendjudgeservice.judge;

import com.yupi.yuojbackendjudgeservice.judge.codesandbox.impl.ThirdPartyCodeSandbox;
import com.yupi.yuojbackendmodel.model.codesandbox.ExecuteCodeRequest;
import com.yupi.yuojbackendmodel.model.codesandbox.ExecuteCodeResponse;
import com.yupi.yuojbackendmodel.model.codesandbox.JudgeInfo;
import com.yupi.yuojbackendmodel.model.dto.question.JudgeCase;
import com.yupi.yuojbackendmodel.model.entity.Question;
import com.yupi.yuojbackendmodel.model.entity.QuestionSubmit;
import com.yupi.yuojbackendmodel.model.enums.QuestionSubmitStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Judge0判题服务测试
 */
@Slf4j
@SpringBootTest
public class Judge0JudgeServiceTest {

    @Mock
    private ThirdPartyCodeSandbox codeSandbox;

    private Judge0JudgeService judge0JudgeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        judge0JudgeService = new Judge0JudgeService();
    }

    @Test
    void testJudgeCodeSuccess() {
        // 模拟成功的代码执行
        JudgeInfo judgeInfo1 = new JudgeInfo();
        judgeInfo1.setMessage("执行成功");
        judgeInfo1.setMemory(1024L);
        judgeInfo1.setTime(100L);
        ExecuteCodeResponse mockResponse = ExecuteCodeResponse.builder()
                .status(0)
                .message("执行成功")
                .outputList(Arrays.asList("Hello World", "42"))
                .judgeInfo(judgeInfo1)
                .build();

        when(codeSandbox.executeCode(any(ExecuteCodeRequest.class))).thenReturn(mockResponse);

        // 创建测试数据
        Question question = new Question();
        question.setJudgeCase("[{\"input\":\"\",\"output\":\"Hello World\"},{\"input\":\"\",\"output\":\"42\"}]");

        QuestionSubmit questionSubmit = new QuestionSubmit();
        questionSubmit.setLanguage("python");
        questionSubmit.setCode("print('Hello World')\nprint('42')");
        questionSubmit.setStatus(QuestionSubmitStatusEnum.WAITING.getValue());

        // 测试判题逻辑
        JudgeInfo result = judge0JudgeService.judgeCode(mockResponse, question);
        
        assertNotNull(result);
        assertEquals("答案正确", result.getMessage());
    }

    @Test
    void testJudgeCodeWrongAnswer() {
        // 模拟答案错误的代码执行
        JudgeInfo judgeInfo2 = new JudgeInfo();
        judgeInfo2.setMessage("执行成功");
        judgeInfo2.setMemory(1024L);
        judgeInfo2.setTime(100L);
        ExecuteCodeResponse mockResponse = ExecuteCodeResponse.builder()
                .status(0)
                .message("执行成功")
                .outputList(Arrays.asList("Wrong Answer"))
                .judgeInfo(judgeInfo2)
                .build();

        when(codeSandbox.executeCode(any(ExecuteCodeRequest.class))).thenReturn(mockResponse);

        // 创建测试数据
        Question question = new Question();
        question.setJudgeCase("[{\"input\":\"\",\"output\":\"Hello World\"}]");

        QuestionSubmit questionSubmit = new QuestionSubmit();
        questionSubmit.setLanguage("python");
        questionSubmit.setCode("print('Wrong Answer')");
        questionSubmit.setStatus(QuestionSubmitStatusEnum.WAITING.getValue());

        // 测试判题逻辑
        JudgeInfo result = judge0JudgeService.judgeCode(mockResponse, question);
        
        assertNotNull(result);
        assertTrue(result.getMessage().contains("答案错误"));
    }

    @Test
    void testJudgeCodeCompilationError() {
        // 模拟编译错误的代码执行
        JudgeInfo judgeInfo3 = new JudgeInfo();
        judgeInfo3.setMessage("编译错误: syntax error");
        judgeInfo3.setMemory(0L);
        judgeInfo3.setTime(0L);
        ExecuteCodeResponse mockResponse = ExecuteCodeResponse.builder()
                .status(2)
                .message("编译错误")
                .outputList(Arrays.asList())
                .judgeInfo(judgeInfo3)
                .build();

        when(codeSandbox.executeCode(any(ExecuteCodeRequest.class))).thenReturn(mockResponse);

        // 创建测试数据
        Question question = new Question();
        question.setJudgeCase("[{\"input\":\"\",\"output\":\"Hello World\"}]");

        QuestionSubmit questionSubmit = new QuestionSubmit();
        questionSubmit.setLanguage("python");
        questionSubmit.setCode("print('Hello World'"); // 故意制造语法错误
        questionSubmit.setStatus(QuestionSubmitStatusEnum.WAITING.getValue());

        // 测试判题逻辑
        JudgeInfo result = judge0JudgeService.judgeCode(mockResponse, question);
        
        assertNotNull(result);
        assertTrue(result.getMessage().contains("执行失败"));
    }

    @Test
    void testJudgeCodeRuntimeError() {
        // 模拟运行时错误的代码执行
        JudgeInfo judgeInfo4 = new JudgeInfo();
        judgeInfo4.setMessage("运行时错误: division by zero");
        judgeInfo4.setMemory(512L);
        judgeInfo4.setTime(50L);
        ExecuteCodeResponse mockResponse = ExecuteCodeResponse.builder()
                .status(3)
                .message("运行时错误")
                .outputList(Arrays.asList())
                .judgeInfo(judgeInfo4)
                .build();

        when(codeSandbox.executeCode(any(ExecuteCodeRequest.class))).thenReturn(mockResponse);

        // 创建测试数据
        Question question = new Question();
        question.setJudgeCase("[{\"input\":\"\",\"output\":\"Hello World\"}]");

        QuestionSubmit questionSubmit = new QuestionSubmit();
        questionSubmit.setLanguage("python");
        questionSubmit.setCode("print(1/0)"); // 故意制造运行时错误
        questionSubmit.setStatus(QuestionSubmitStatusEnum.WAITING.getValue());

        // 测试判题逻辑
        JudgeInfo result = judge0JudgeService.judgeCode(mockResponse, question);
        
        assertNotNull(result);
        assertTrue(result.getMessage().contains("执行失败"));
    }

    @Test
    void testJudgeCodeTimeLimitExceeded() {
        // 模拟时间超限的代码执行
        JudgeInfo judgeInfo5 = new JudgeInfo();
        judgeInfo5.setMessage("时间超限");
        judgeInfo5.setMemory(1024L);
        judgeInfo5.setTime(5000L);
        ExecuteCodeResponse mockResponse = ExecuteCodeResponse.builder()
                .status(4)
                .message("时间超限")
                .outputList(Arrays.asList())
                .judgeInfo(judgeInfo5)
                .build();

        when(codeSandbox.executeCode(any(ExecuteCodeRequest.class))).thenReturn(mockResponse);

        // 创建测试数据
        Question question = new Question();
        question.setJudgeCase("[{\"input\":\"\",\"output\":\"Hello World\"}]");

        QuestionSubmit questionSubmit = new QuestionSubmit();
        questionSubmit.setLanguage("python");
        questionSubmit.setCode("while True: pass"); // 无限循环
        questionSubmit.setStatus(QuestionSubmitStatusEnum.WAITING.getValue());

        // 测试判题逻辑
        JudgeInfo result = judge0JudgeService.judgeCode(mockResponse, question);
        
        assertNotNull(result);
        assertTrue(result.getMessage().contains("执行失败"));
    }

    @Test
    void testJudgeCodeMemoryLimitExceeded() {
        // 模拟内存超限的代码执行
        JudgeInfo judgeInfo6 = new JudgeInfo();
        judgeInfo6.setMessage("内存超限");
        judgeInfo6.setMemory(1024000L);
        judgeInfo6.setTime(100L);
        ExecuteCodeResponse mockResponse = ExecuteCodeResponse.builder()
                .status(5)
                .message("内存超限")
                .outputList(Arrays.asList())
                .judgeInfo(judgeInfo6)
                .build();

        when(codeSandbox.executeCode(any(ExecuteCodeRequest.class))).thenReturn(mockResponse);

        // 创建测试数据
        Question question = new Question();
        question.setJudgeCase("[{\"input\":\"\",\"output\":\"Hello World\"}]");

        QuestionSubmit questionSubmit = new QuestionSubmit();
        questionSubmit.setLanguage("python");
        questionSubmit.setCode("a = [0] * 1000000000"); // 大量内存分配
        questionSubmit.setStatus(QuestionSubmitStatusEnum.WAITING.getValue());

        // 测试判题逻辑
        JudgeInfo result = judge0JudgeService.judgeCode(mockResponse, question);
        
        assertNotNull(result);
        assertTrue(result.getMessage().contains("执行失败"));
    }

    @Test
    void testJudgeCodeOutputMismatch() {
        // 模拟输出数量不匹配
        JudgeInfo judgeInfo7 = new JudgeInfo();
        judgeInfo7.setMessage("执行成功");
        judgeInfo7.setMemory(1024L);
        judgeInfo7.setTime(100L);
        ExecuteCodeResponse mockResponse = ExecuteCodeResponse.builder()
                .status(0)
                .message("执行成功")
                .outputList(Arrays.asList("Hello World")) // 只有1个输出
                .judgeInfo(judgeInfo7)
                .build();

        when(codeSandbox.executeCode(any(ExecuteCodeRequest.class))).thenReturn(mockResponse);

        // 创建测试数据 - 期望2个输出
        Question question = new Question();
        question.setJudgeCase("[{\"input\":\"\",\"output\":\"Hello World\"},{\"input\":\"\",\"output\":\"42\"}]");

        QuestionSubmit questionSubmit = new QuestionSubmit();
        questionSubmit.setLanguage("python");
        questionSubmit.setCode("print('Hello World')"); // 只输出1行
        questionSubmit.setStatus(QuestionSubmitStatusEnum.WAITING.getValue());

        // 测试判题逻辑
        JudgeInfo result = judge0JudgeService.judgeCode(mockResponse, question);
        
        assertNotNull(result);
        assertTrue(result.getMessage().contains("输出数量不匹配"));
    }

    @Test
    void testJudgeCodeWithMultipleTestCases() {
        // 测试多个测试用例的情况
        JudgeInfo judgeInfo8 = new JudgeInfo();
        judgeInfo8.setMessage("执行成功");
        judgeInfo8.setMemory(1024L);
        judgeInfo8.setTime(100L);
        ExecuteCodeResponse mockResponse = ExecuteCodeResponse.builder()
                .status(0)
                .message("执行成功")
                .outputList(Arrays.asList("Hello", "World", "42"))
                .judgeInfo(judgeInfo8)
                .build();

        when(codeSandbox.executeCode(any(ExecuteCodeRequest.class))).thenReturn(mockResponse);

        // 创建测试数据
        Question question = new Question();
        question.setJudgeCase("[{\"input\":\"\",\"output\":\"Hello\"},{\"input\":\"\",\"output\":\"World\"},{\"input\":\"\",\"output\":\"42\"}]");

        QuestionSubmit questionSubmit = new QuestionSubmit();
        questionSubmit.setLanguage("python");
        questionSubmit.setCode("print('Hello')\nprint('World')\nprint('42')");
        questionSubmit.setStatus(QuestionSubmitStatusEnum.WAITING.getValue());

        // 测试判题逻辑
        JudgeInfo result = judge0JudgeService.judgeCode(mockResponse, question);
        
        assertNotNull(result);
        assertEquals("答案正确", result.getMessage());
    }
} 