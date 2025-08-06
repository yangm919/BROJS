# Maximum Subarray - 多种解法

## 题目描述
给定一个整数数组 nums，找到具有最大和的连续子数组，返回其最大和。

## 解法1：Kadane算法（动态规划）- O(n)
```java
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String inputLine = scanner.nextLine();
        
        String numbersStr = inputLine.replace("[", "").replace("]", "");
        String[] numberStrings = numbersStr.split(",");
        
        int[] nums = new int[numberStrings.length];
        for (int i = 0; i < numberStrings.length; i++) {
            nums[i] = Integer.parseInt(numberStrings[i].trim());
        }
        
        int maxSum = maxSubArray(nums);
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
            // 要么扩展当前子数组，要么开始新的子数组
            currentSum = Math.max(nums[i], currentSum + nums[i]);
            maxSum = Math.max(maxSum, currentSum);
        }
        
        return maxSum;
    }
}
```

## 解法2：分治法 - O(n log n)
```java
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String inputLine = scanner.nextLine();
        
        String numbersStr = inputLine.replace("[", "").replace("]", "");
        String[] numberStrings = numbersStr.split(",");
        
        int[] nums = new int[numberStrings.length];
        for (int i = 0; i < numberStrings.length; i++) {
            nums[i] = Integer.parseInt(numberStrings[i].trim());
        }
        
        int maxSum = maxSubArrayDivideAndConquer(nums);
        System.out.println(maxSum);
        
        scanner.close();
    }
    
    public static int maxSubArrayDivideAndConquer(int[] nums) {
        return divideAndConquer(nums, 0, nums.length - 1);
    }
    
    private static int divideAndConquer(int[] nums, int left, int right) {
        if (left == right) {
            return nums[left];
        }
        
        int mid = left + (right - left) / 2;
        
        // 递归求解左半部分和右半部分
        int leftMax = divideAndConquer(nums, left, mid);
        int rightMax = divideAndConquer(nums, mid + 1, right);
        
        // 计算跨越中点的最大子数组和
        int crossMax = maxCrossingSubarray(nums, left, mid, right);
        
        return Math.max(Math.max(leftMax, rightMax), crossMax);
    }
    
    private static int maxCrossingSubarray(int[] nums, int left, int mid, int right) {
        // 计算左半部分的最大后缀和
        int leftSum = Integer.MIN_VALUE;
        int sum = 0;
        for (int i = mid; i >= left; i--) {
            sum += nums[i];
            leftSum = Math.max(leftSum, sum);
        }
        
        // 计算右半部分的最大前缀和
        int rightSum = Integer.MIN_VALUE;
        sum = 0;
        for (int i = mid + 1; i <= right; i++) {
            sum += nums[i];
            rightSum = Math.max(rightSum, sum);
        }
        
        return leftSum + rightSum;
    }
}
```

## 解法3：暴力解法 - O(n²)
```java
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String inputLine = scanner.nextLine();
        
        String numbersStr = inputLine.replace("[", "").replace("]", "");
        String[] numberStrings = numbersStr.split(",");
        
        int[] nums = new int[numberStrings.length];
        for (int i = 0; i < numberStrings.length; i++) {
            nums[i] = Integer.parseInt(numberStrings[i].trim());
        }
        
        int maxSum = maxSubArrayBruteForce(nums);
        System.out.println(maxSum);
        
        scanner.close();
    }
    
    public static int maxSubArrayBruteForce(int[] nums) {
        int maxSum = Integer.MIN_VALUE;
        
        for (int i = 0; i < nums.length; i++) {
            int currentSum = 0;
            for (int j = i; j < nums.length; j++) {
                currentSum += nums[j];
                maxSum = Math.max(maxSum, currentSum);
            }
        }
        
        return maxSum;
    }
}
```

## 算法分析

### 时间复杂度
- **Kadane算法**：O(n) - 最优解
- **分治法**：O(n log n) - 理论价值高
- **暴力解法**：O(n²) - 不推荐用于大数据

### 空间复杂度
- 所有解法：O(1) - 只使用常数额外空间

### 适用场景
- **Kadane算法**：实际应用中最常用，效率最高
- **分治法**：适合教学和理解算法思想
- **暴力解法**：仅用于理解问题本质

## 测试用例
- `[-2,1,-3,4,-1,2,1,-5,4]` → `6`
- `[1]` → `1`
- `[5,4,-1,7,8]` → `23`
- `[-1]` → `-1`
- `[-2,-1]` → `-1` 