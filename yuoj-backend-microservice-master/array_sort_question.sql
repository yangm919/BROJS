-- Add Array Sort question
INSERT INTO `question` (
    `title`, 
    `content`, 
    `tags`, 
    `answer`, 
    `judgeCase`, 
    `judgeConfig`, 
    `userId`,
    `createTime`,
    `updateTime`,
    `isDelete`,
    `submitNum`,
    `acceptedNum`
) VALUES (
    'Array Sort',
    'Given an integer array, sort it in ascending order and return the sorted array.

Example 1:
Input: [3,1,4,1,5,9,2,6]
Output: [1,1,2,3,4,5,6,9]

Example 2:
Input: [5,2,3,1]
Output: [1,2,3,5]

Example 3:
Input: [1]
Output: [1]

Constraints:
1 <= nums.length <= 1000
-1000 <= nums[i] <= 1000

Note: You can use any sorting algorithm (bubble sort, quick sort, merge sort, etc.)',
    '["Array", "Sorting", "Algorithm"]',
    '```java
import java.util.Scanner;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        // Read input array
        String inputLine = scanner.nextLine();
        
        // Parse array format [3,1,4,1,5,9,2,6] -> extract numbers
        String numbersStr = inputLine.replace("[", "").replace("]", "");
        String[] numberStrings = numbersStr.split(",");
        
        // Convert to int array
        int[] nums = new int[numberStrings.length];
        for (int i = 0; i < numberStrings.length; i++) {
            nums[i] = Integer.parseInt(numberStrings[i].trim());
        }
        
        // Sort the array
        Arrays.sort(nums);
        
        // Output result in array format
        System.out.print("[");
        for (int i = 0; i < nums.length; i++) {
            System.out.print(nums[i]);
            if (i < nums.length - 1) {
                System.out.print(",");
            }
        }
        System.out.println("]");
        
        scanner.close();
    }
}
```',
    '[{"input": "[3,1,4,1,5,9,2,6]", "output": "[1,1,2,3,4,5,6,9]"}, {"input": "[5,2,3,1]", "output": "[1,2,3,5]"}, {"input": "[1]", "output": "[1]"}, {"input": "[9,8,7,6,5,4,3,2,1]", "output": "[1,2,3,4,5,6,7,8,9]"}, {"input": "[0,0,0,0]", "output": "[0,0,0,0]"}]',
    '{"timeLimit": 1000, "memoryLimit": 256000}',
    1,
    NOW(),
    NOW(),
    0,
    0,
    0
); 