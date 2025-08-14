package com.yupi.yuojbackendjudgeservice.judge.codesandbox.impl;

import com.yupi.yuojbackendjudgeservice.judge.codesandbox.CodeSandbox;
import com.yupi.yuojbackendmodel.model.codesandbox.ExecuteCodeRequest;
import com.yupi.yuojbackendmodel.model.codesandbox.ExecuteCodeResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * Judge0使用示例
 * 展示如何使用Judge0 API执行不同编程语言的代码
 */
@Slf4j
@Component
public class Judge0UsageExample {

    @Autowired
    private ThirdPartyCodeSandbox codeSandbox;

    /**
     * Java代码执行示例
     */
    public void executeJavaExample() {
        String javaCode = "public class Main {\n" +
                "    public static void main(String[] args) {\n" +
                "        System.out.println(\"Hello from Java!\");\n" +
                "        System.out.println(\"1 + 2 = \" + (1 + 2));\n" +
                "    }\n" +
                "}";

        ExecuteCodeRequest request = ExecuteCodeRequest.builder()
                .code(javaCode)
                .language("java")
                .inputList(Arrays.asList(""))
                .build();

        ExecuteCodeResponse response = codeSandbox.executeCode(request);
        log.info("Java execution result: {}", response.getMessage());
        log.info("Output: {}", response.getOutputList());
    }

    /**
     * Python代码执行示例
     */
    public void executePythonExample() {
        String pythonCode =
                "print(\"Hello from Python!\")\n" +
                "print(\"1 + 2 =\", 1 + 2)\n" +
                "\n" +
                "# 计算斐波那契数列\n" +
                "def fibonacci(n):\n" +
                "    if n <= 1:\n" +
                "        return n\n" +
                "    return fibonacci(n-1) + fibonacci(n-2)\n" +
                "\n" +
                "print(\"Fibonacci(10) =\", fibonacci(10))\n";

        ExecuteCodeRequest request = ExecuteCodeRequest.builder()
                .code(pythonCode)
                .language("python")
                .inputList(Arrays.asList(""))
                .build();

        ExecuteCodeResponse response = codeSandbox.executeCode(request);
        log.info("Python execution result: {}", response.getMessage());
        log.info("Output: {}", response.getOutputList());
    }

    /**
     * C++代码执行示例
     */
    public void executeCppExample() {
        String cppCode =
                "#include <iostream>\n" +
                "#include <vector>\n" +
                "\n" +
                "int main() {\n" +
                "    std::cout << \"Hello from C++!\" << std::endl;\n" +
                "    std::cout << \"1 + 2 = \" << (1 + 2) << std::endl;\n" +
                "    std::vector<int> numbers = {1, 2, 3, 4, 5};\n" +
                "    int sum = 0;\n" +
                "    for (int num : numbers) {\n" +
                "        sum += num;\n" +
                "    }\n" +
                "    std::cout << \"Sum of numbers: \" << sum << std::endl;\n" +
                "    return 0;\n" +
                "}\n";

        ExecuteCodeRequest request = ExecuteCodeRequest.builder()
                .code(cppCode)
                .language("cpp")
                .inputList(Arrays.asList(""))
                .build();

        ExecuteCodeResponse response = codeSandbox.executeCode(request);
        log.info("C++ execution result: {}", response.getMessage());
        log.info("Output: {}", response.getOutputList());
    }

    /**
     * JavaScript代码执行示例
     */
    public void executeJavaScriptExample() {
        String jsCode =
                "console.log(\"Hello from JavaScript!\");\n" +
                "console.log(\"1 + 2 =\", 1 + 2);\n" +
                "\n" +
                "// Array operation\n" +
                "const numbers = [1, 2, 3, 4, 5];\n" +
                "const sum = numbers.reduce((acc, num) => acc + num, 0);\n" +
                "console.log(\"Sum of numbers:\", sum);\n" +
                "\n" +
                "// Function example\n" +
                "function factorial(n) {\n" +
                "    if (n <= 1) return 1;\n" +
                "    return n * factorial(n - 1);\n" +
                "}\n" +
                "console.log(\"Factorial(5) =\", factorial(5));\n";

        ExecuteCodeRequest request = ExecuteCodeRequest.builder()
                .code(jsCode)
                .language("javascript")
                .inputList(Arrays.asList(""))
                .build();

        ExecuteCodeResponse response = codeSandbox.executeCode(request);
        log.info("JavaScript execution result: {}", response.getMessage());
        log.info("Output: {}", response.getOutputList());
    }

    /**
     * 带输入参数的代码执行示例
     */
    public void executeWithInputExample() {
        String pythonCode =
                "import sys\n" +
                "\n" +
                "# Read input\n" +
                "input_data = input().strip()\n" +
                "print(f\"Received input: {input_data}\")\n" +
                "\n" +
                "# Process input\n" +
                "try:\n" +
                "    number = int(input_data)\n" +
                "    result = number * 2\n" +
                "    print(f\"Double of {number} is {result}\")\n" +
                "except ValueError:\n" +
                "    print(\"Invalid input, please provide a number\")\n";

        ExecuteCodeRequest request = ExecuteCodeRequest.builder()
                .code(pythonCode)
                .language("python")
                .inputList(Arrays.asList("42")) // 输入参数
                .build();

        ExecuteCodeResponse response = codeSandbox.executeCode(request);
        log.info("With input execution result: {}", response.getMessage());
        log.info("Output: {}", response.getOutputList());
    }

    /**
     * 错误处理示例
     */
    public void executeErrorExample() {
        // 故意写一个错误的Java代码
        String errorCode =
                "public class Main {\n" +
                "    public static void main(String[] args) {\n" +
                "        System.out.println(\"Hello World\");\n" +
                "        // Make a compilation error\n" +
                "        undefinedVariable = 10;\n" +
                "    }\n" +
                "}\n";

        ExecuteCodeRequest request = ExecuteCodeRequest.builder()
                .code(errorCode)
                .language("java")
                .inputList(Arrays.asList(""))
                .build();

        ExecuteCodeResponse response = codeSandbox.executeCode(request);
        log.info("wrong code execution result: {}", response.getMessage());
        log.info("status: {}", response.getStatus());
        if (response.getJudgeInfo() != null) {
            log.info("error message: {}", response.getJudgeInfo().getMessage());
        }
    }

    /**
     * 性能测试示例
     */
    public void executePerformanceTest() {
        String performanceCode =
                "import time\n" +
                "\n" +
                "start_time = time.time()\n" +
                "\n" +
                "# Execute some calculation-intensive operations\n" +
                "result = 0\n" +
                "for i in range(1000000):\n" +
                "    result += i\n" +
                "\n" +
                "end_time = time.time()\n" +
                "execution_time = end_time - start_time\n" +
                "\n" +
                "print(f\"Calculation result: {result}\")\n" +
                "print(f\"Execution time: {execution_time:.4f} seconds\")\n";

        ExecuteCodeRequest request = ExecuteCodeRequest.builder()
                .code(performanceCode)
                .language("python")
                .inputList(Arrays.asList(""))
                .build();

        ExecuteCodeResponse response = codeSandbox.executeCode(request);
        log.info("Performance test result: {}", response.getMessage());
        log.info("Output: {}", response.getOutputList());
        
        if (response.getJudgeInfo() != null) {
            log.info("Memory usage: {} KB", response.getJudgeInfo().getMemory());
            log.info("Execution time: {} ms", response.getJudgeInfo().getTime());
        }
    }

    /**
     * 运行所有示例
     */
    public void runAllExamples() {
        log.info("Start running Judge0 usage examples...");
        
        try {
            executeJavaExample();
            Thread.sleep(1000); // 避免API调用过于频繁
            
            executePythonExample();
            Thread.sleep(1000);
            
            executeCppExample();
            Thread.sleep(1000);
            
            executeJavaScriptExample();
            Thread.sleep(1000);
            
            executeWithInputExample();
            Thread.sleep(1000);
            
            executeErrorExample();
            Thread.sleep(1000);
            
            executePerformanceTest();
            
        } catch (InterruptedException e) {
            log.error("Example execution interrupted", e);
        }
        
        log.info("All examples executed successfully");
    }
} 