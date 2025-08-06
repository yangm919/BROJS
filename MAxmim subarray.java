import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        // 读取输入数组
        String inputLine = scanner.nextLine();
        
        // 解析数组格式 [-2,1,-3,4,-1,2,1,-5,4] -> 提取数字
        String numbersStr = inputLine.replace("[", "").replace("]", "");
        String[] numberStrings = numbersStr.split(",");
        
        // 转换为int数组
        int[] nums = new int[numberStrings.length];
        for (int i = 0; i < numberStrings.length; i++) {
            nums[i] = Integer.parseInt(numberStrings[i].trim());
        }
        
        // 使用Kadane算法计算最大子数组和
        int maxSum = maxSubArray(nums);
        
        // 输出结果
        System.out.println(maxSum);
        
        scanner.close();
    }
    
    /**
     * Kadane算法 - 动态规划解法
     * 时间复杂度: O(n)
     * 空间复杂度: O(1)
     */
    public static int maxSubArray(int[] nums) {
        if (nums == null || nums.length == 0) {
            return 0;
        }
        
        int maxSum = nums[0];  // 全局最大和
        int currentSum = nums[0];  // 当前子数组的和
        
        for (int i = 1; i < nums.length; i++) {
            // 核心思想：要么扩展当前子数组，要么开始新的子数组
            currentSum = Math.max(nums[i], currentSum + nums[i]);
            maxSum = Math.max(maxSum, currentSum);
        }
        
        return maxSum;
    }
}