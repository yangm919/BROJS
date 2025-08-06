-- Add Maximum Subarray question
INSERT INTO `question` (
    `title`, 
    `content`, 
    `tags`, 
    `answer`, 
    `judgeCase`, 
    `judgeConfig`, 
    `userId`
) VALUES (
    'Maximum Subarray',
    'Given an integer array nums, find the subarray with the largest sum, and return its sum.

A subarray is a contiguous part of an array.

Example 1:
Input: nums = [-2,1,-3,4,-1,2,1,-5,4]
Output: 6
Explanation: The subarray [4,-1,2,1] has the largest sum 6.

Example 2:
Input: nums = [1]
Output: 1

Example 3:
Input: nums = [5,4,-1,7,8]
Output: 23

Constraints:
1 <= nums.length <= 105
-104 <= nums[i] <= 104

Follow up: If you have figured out the O(n) solution, try coding another solution using the divide and conquer approach, which is more subtle.',
    '["Array", "Dynamic Programming", "Divide and Conquer"]',
    '```java
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        // Read input array
        String inputLine = scanner.nextLine();
        
        // Parse array format [-2,1,-3,4,-1,2,1,-5,4] -> extract numbers
        String numbersStr = inputLine.replace("[", "").replace("]", "");
        String[] numberStrings = numbersStr.split(",");
        
        // Convert to int array
        int[] nums = new int[numberStrings.length];
        for (int i = 0; i < numberStrings.length; i++) {
            nums[i] = Integer.parseInt(numberStrings[i].trim());
        }
        
        // Calculate maximum subarray sum using Kadane algorithm
        int maxSum = maxSubArray(nums);
        
        // Output result
        System.out.println(maxSum);
        
        scanner.close();
    }
    
    public static int maxSubArray(int[] nums) {
        if (nums == null || nums.length == 0) {
            return 0;
        }
        
        int maxSum = nums[0];
        int currentSum = nums[0];
        
        for (int i = 1; i < nums.length; i++) {
            // Either extend the current subarray or start a new one
            currentSum = Math.max(nums[i], currentSum + nums[i]);
            maxSum = Math.max(maxSum, currentSum);
        }
        
        return maxSum;
    }
}
```',
    '[{"input": "[-2,1,-3,4,-1,2,1,-5,4]", "output": "6"}, {"input": "[1]", "output": "1"}, {"input": "[5,4,-1,7,8]", "output": "23"}, {"input": "[-1]", "output": "-1"}, {"input": "[-2,-1]", "output": "-1"}]',
    '{"timeLimit": 1000, "memoryLimit": 256000}',
    1
); 