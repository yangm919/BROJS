-- 添加简单加法题目
INSERT INTO `question` (
    `title`, 
    `content`, 
    `tags`, 
    `answer`, 
    `judgeCase`, 
    `judgeConfig`, 
    `userId`
) VALUES (
    '简单加法',
    '给定一个整数数组，计算数组中所有数字的总和。

示例：
输入：[1,2]
输出：3

要求：
1. 输入格式为数组，如 [1,2,3]
2. 输出格式为整数
3. 数组长度不超过100
4. 数组元素为整数，范围在-1000到1000之间',
    '["数组", "基础算法"]',
    '```java
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        // 读取输入数组
        String inputLine = scanner.nextLine();
        
        // 解析数组格式 [1,2] -> 提取数字
        String numbersStr = inputLine.replace("[", "").replace("]", "");
        String[] numberStrings = numbersStr.split(",");
        
        // 计算总和
        int sum = 0;
        for (String numStr : numberStrings) {
            sum += Integer.parseInt(numStr.trim());
        }
        
        // 输出结果
        System.out.println(sum);
        
        scanner.close();
    }
}
```',
    '[{"input": "[1,2]", "output": "3"}]',
    '{"timeLimit": 1000, "memoryLimit": 256000}',
    1
); 