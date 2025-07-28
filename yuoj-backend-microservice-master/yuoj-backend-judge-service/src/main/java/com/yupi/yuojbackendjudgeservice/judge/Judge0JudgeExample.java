package com.yupi.yuojbackendjudgeservice.judge;

import com.yupi.yuojbackendjudgeservice.judge.codesandbox.impl.ThirdPartyCodeSandbox;
import com.yupi.yuojbackendmodel.model.codesandbox.ExecuteCodeRequest;
import com.yupi.yuojbackendmodel.model.codesandbox.ExecuteCodeResponse;
import com.yupi.yuojbackendmodel.model.codesandbox.JudgeInfo;
import com.yupi.yuojbackendmodel.model.dto.question.JudgeCase;
import com.yupi.yuojbackendmodel.model.entity.Question;
import com.yupi.yuojbackendmodel.model.entity.QuestionSubmit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * Judge0判题功能使用示例
 */
@Slf4j
@Component
public class Judge0JudgeExample {

    @Autowired
    private ThirdPartyCodeSandbox codeSandbox;

    @Autowired
    private Judge0JudgeService judge0JudgeService;

    /**
     * 示例1：简单的Hello World判题
     */
    public void example1_HelloWorld() {
        log.info("=== 示例1：Hello World判题 ===");

        // 创建题目
        Question question = new Question();
        question.setJudgeCase("[{\"input\":\"\",\"output\":\"Hello World\"}]");

        // 创建提交
        QuestionSubmit questionSubmit = new QuestionSubmit();
        questionSubmit.setLanguage("python");
        questionSubmit.setCode("print('Hello World')");

        // 执行判题
        ExecuteCodeRequest request = ExecuteCodeRequest.builder()
                .code(questionSubmit.getCode())
                .language(questionSubmit.getLanguage())
                .inputList(Arrays.asList(""))
                .build();

        ExecuteCodeResponse response = codeSandbox.executeCode(request);
        JudgeInfo judgeInfo = judge0JudgeService.judgeCode(response, question);

        log.info("判题结果: {}", judgeInfo.getMessage());
        log.info("内存使用: {} KB", judgeInfo.getMemory());
        log.info("执行时间: {} ms", judgeInfo.getTime());
    }

    /**
     * 示例2：数学计算判题
     */
    public void example2_MathCalculation() {
        log.info("=== 示例2：数学计算判题 ===");

        // 创建题目
        Question question = new Question();
        question.setJudgeCase("[{\"input\":\"5 3\",\"output\":\"8\"},{\"input\":\"10 20\",\"output\":\"30\"}]");

        // 创建提交
        QuestionSubmit questionSubmit = new QuestionSubmit();
        questionSubmit.setLanguage("python");
        questionSubmit.setCode(
                "a, b = map(int, input().split())\n" +
                "print(a + b)\n"
        );

        // 执行判题
        ExecuteCodeRequest request = ExecuteCodeRequest.builder()
                .code(questionSubmit.getCode())
                .language(questionSubmit.getLanguage())
                .inputList(Arrays.asList("5 3", "10 20"))
                .build();

        ExecuteCodeResponse response = codeSandbox.executeCode(request);
        JudgeInfo judgeInfo = judge0JudgeService.judgeCode(response, question);

        log.info("判题结果: {}", judgeInfo.getMessage());
        log.info("输出: {}", response.getOutputList());
    }

    /**
     * 示例3：Java判题
     */
    public void example3_JavaJudge() {
        log.info("=== 示例3：Java判题 ===");

        // 创建题目
        Question question = new Question();
        question.setJudgeCase("[{\"input\":\"\",\"output\":\"Hello from Java!\"}]");

        // 创建提交
        QuestionSubmit questionSubmit = new QuestionSubmit();
        questionSubmit.setLanguage("java");
        questionSubmit.setCode(
                "public class Main {\n" +
                "    public static void main(String[] args) {\n" +
                "        System.out.println(\"Hello from Java!\");\n" +
                "    }\n" +
                "}\n"
        );

        // 执行判题
        ExecuteCodeRequest request = ExecuteCodeRequest.builder()
                .code(questionSubmit.getCode())
                .language(questionSubmit.getLanguage())
                .inputList(Arrays.asList(""))
                .build();

        ExecuteCodeResponse response = codeSandbox.executeCode(request);
        JudgeInfo judgeInfo = judge0JudgeService.judgeCode(response, question);

        log.info("判题结果: {}", judgeInfo.getMessage());
        log.info("输出: {}", response.getOutputList());
    }

    /**
     * 示例4：JavaScript判题
     */
    public void example4_JavaScriptJudge() {
        log.info("=== 示例4：JavaScript判题 ===");

        // 创建题目
        Question question = new Question();
        question.setJudgeCase("[{\"input\":\"\",\"output\":\"Hello from JavaScript!\"}]");

        // 创建提交
        QuestionSubmit questionSubmit = new QuestionSubmit();
        questionSubmit.setLanguage("javascript");
        questionSubmit.setCode("console.log('Hello from JavaScript!');");

        // 执行判题
        ExecuteCodeRequest request = ExecuteCodeRequest.builder()
                .code(questionSubmit.getCode())
                .language(questionSubmit.getLanguage())
                .inputList(Arrays.asList(""))
                .build();

        ExecuteCodeResponse response = codeSandbox.executeCode(request);
        JudgeInfo judgeInfo = judge0JudgeService.judgeCode(response, question);

        log.info("判题结果: {}", judgeInfo.getMessage());
        log.info("输出: {}", response.getOutputList());
    }

    /**
     * 示例5：错误代码判题
     */
    public void example5_ErrorCode() {
        log.info("=== 示例5：错误代码判题 ===");

        // 创建题目
        Question question = new Question();
        question.setJudgeCase("[{\"input\":\"\",\"output\":\"Hello World\"}]");

        // 创建提交（故意写错）
        QuestionSubmit questionSubmit = new QuestionSubmit();
        questionSubmit.setLanguage("python");
        questionSubmit.setCode("print('Hello World'"); // 缺少右括号

        // 执行判题
        ExecuteCodeRequest request = ExecuteCodeRequest.builder()
                .code(questionSubmit.getCode())
                .language(questionSubmit.getLanguage())
                .inputList(Arrays.asList(""))
                .build();

        ExecuteCodeResponse response = codeSandbox.executeCode(request);
        JudgeInfo judgeInfo = judge0JudgeService.judgeCode(response, question);

        log.info("判题结果: {}", judgeInfo.getMessage());
        log.info("错误信息: {}", response.getMessage());
    }

    /**
     * 示例6：性能测试判题
     */
    public void example6_PerformanceTest() {
        log.info("=== 示例6：性能测试判题 ===");

        // 创建题目
        Question question = new Question();
        question.setJudgeCase("[{\"input\":\"1000\",\"output\":\"500500\"}]");

        // 创建提交
        QuestionSubmit questionSubmit = new QuestionSubmit();
        questionSubmit.setLanguage("python");
        questionSubmit.setCode(
                "n = int(input())\n" +
                "result = sum(range(1, n + 1))\n" +
                "print(result)\n"
        );

        // 执行判题
        ExecuteCodeRequest request = ExecuteCodeRequest.builder()
                .code(questionSubmit.getCode())
                .language(questionSubmit.getLanguage())
                .inputList(Arrays.asList("1000"))
                .build();

        ExecuteCodeResponse response = codeSandbox.executeCode(request);
        JudgeInfo judgeInfo = judge0JudgeService.judgeCode(response, question);

        log.info("判题结果: {}", judgeInfo.getMessage());
        log.info("内存使用: {} KB", judgeInfo.getMemory());
        log.info("执行时间: {} ms", judgeInfo.getTime());
        log.info("输出: {}", response.getOutputList());
    }

    /**
     * 示例7：多语言对比判题
     */
    public void example7_MultiLanguageComparison() {
        log.info("=== 示例7：多语言对比判题 ===");

        String[] languages = {"python", "java", "javascript", "cpp"};
        String[] codes = {
            "print('Hello World')\n",
            "public class Main { public static void main(String[] args) { System.out.println(\"Hello World\"); } }\n",
            "console.log('Hello World');\n",
            "#include <iostream>\nint main() { std::cout << \"Hello World\" << std::endl; return 0; }\n"
        };

        Question question = new Question();
        question.setJudgeCase("[{\"input\":\"\",\"output\":\"Hello World\"}]");

        for (int i = 0; i < languages.length; i++) {
            log.info("--- 测试语言: {} ---", languages[i]);

            QuestionSubmit questionSubmit = new QuestionSubmit();
            questionSubmit.setLanguage(languages[i]);
            questionSubmit.setCode(codes[i]);

            ExecuteCodeRequest request = ExecuteCodeRequest.builder()
                    .code(questionSubmit.getCode())
                    .language(questionSubmit.getLanguage())
                    .inputList(Arrays.asList(""))
                    .build();

            ExecuteCodeResponse response = codeSandbox.executeCode(request);
            JudgeInfo judgeInfo = judge0JudgeService.judgeCode(response, question);

            log.info("判题结果: {}", judgeInfo.getMessage());
            log.info("内存使用: {} KB", judgeInfo.getMemory());
            log.info("执行时间: {} ms", judgeInfo.getTime());
        }
    }

    /**
     * 运行所有示例
     */
    public void runAllExamples() {
        log.info("开始运行Judge0判题示例...");

        try {
            example1_HelloWorld();
            Thread.sleep(1000);

            example2_MathCalculation();
            Thread.sleep(1000);

            example3_JavaJudge();
            Thread.sleep(1000);

            example4_JavaScriptJudge();
            Thread.sleep(1000);

            example5_ErrorCode();
            Thread.sleep(1000);

            example6_PerformanceTest();
            Thread.sleep(1000);

            example7_MultiLanguageComparison();

        } catch (InterruptedException e) {
            log.error("示例执行被中断", e);
        }

        log.info("所有示例执行完成");
    }
} 