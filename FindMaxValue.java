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
        
        // 使用算法计算数组最大值
        int maxValue = findMaxValue(nums);
        
        // 输出结果
        System.out.println(maxValue);
        
        scanner.close();
    }
    
    /**
     * 查找数组中的最大值
     * 时间复杂度: O(n)
     * 空间复杂度: O(1)
     */
    public static int findMaxValue(int[] nums) {
        if (nums == null || nums.length == 0) {
            throw new IllegalArgumentException("数组不能为空");
        }
        
        int maxValue = nums[0];  // 假设第一个元素为最大值
        
        // 遍历数组，比较每个元素
        for (int i = 1; i < nums.length; i++) {
            if (nums[i] > maxValue) {
                maxValue = nums[i];
            }
        }
        
        return maxValue;
    }
}
