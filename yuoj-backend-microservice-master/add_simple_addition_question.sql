-- Add simple addition question
INSERT INTO `question` (
    `title`, 
    `content`, 
    `tags`, 
    `answer`, 
    `judgeCase`, 
    `judgeConfig`, 
    `userId`
) VALUES (
    'Simple Addition',
    'Given an integer array, calculate the sum of all numbers in the array.

Example:
Input: [1,2]
Output: 3

Requirements:
1. Input format is array, such as [1,2,3]
2. Output format is integer
3. Array length does not exceed 100
4. Array elements are integers, range from -1000 to 1000',
    '["Array", "Basic Algorithm"]',
    '```java
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        // Read input array
        String inputLine = scanner.nextLine();
        
        // Parse array format [1,2] -> extract numbers
        String numbersStr = inputLine.replace("[", "").replace("]", "");
        String[] numberStrings = numbersStr.split(",");
        
        // Calculate sum
        int sum = 0;
        for (String numStr : numberStrings) {
            sum += Integer.parseInt(numStr.trim());
        }
        
        // Output result
        System.out.println(sum);
        
        scanner.close();
    }
}
```',
    '[{"input": "[1,2]", "output": "3"}]',
    '{"timeLimit": 1000, "memoryLimit": 256000}',
    1
); 