/**
 * 两数之和 - 暴力解法
 * 时间复杂度: O(n²)
 * 空间复杂度: O(1)
 */
public class TwoSumBruteForce {
    
    public int[] twoSum(int[] nums, int target) {
        // 双重循环遍历所有可能的组合
        for (int i = 0; i < nums.length; i++) {
            for (int j = i + 1; j < nums.length; j++) {
                // 如果找到两个数的和等于目标值
                if (nums[i] + nums[j] == target) {
                    return new int[]{i, j};
                }
            }
        }
        
        // 如果没有找到答案，返回空数组
        return new int[]{};
    }
    
    // 测试方法
    public static void main(String[] args) {
        TwoSumBruteForce solution = new TwoSumBruteForce();
        
        // 测试用例1
        int[] nums1 = {2, 7, 11, 15};
        int target1 = 9;
        int[] result1 = solution.twoSum(nums1, target1);
        System.out.println("测试用例1: nums = [2,7,11,15], target = 9");
        System.out.println("结果: [" + result1[0] + "," + result1[1] + "]");
        
        // 测试用例2
        int[] nums2 = {3, 2, 4};
        int target2 = 6;
        int[] result2 = solution.twoSum(nums2, target2);
        System.out.println("测试用例2: nums = [3,2,4], target = 6");
        System.out.println("结果: [" + result2[0] + "," + result2[1] + "]");
        
        // 测试用例3
        int[] nums3 = {3, 3};
        int target3 = 6;
        int[] result3 = solution.twoSum(nums3, target3);
        System.out.println("测试用例3: nums = [3,3], target = 6");
        System.out.println("结果: [" + result3[0] + "," + result3[1] + "]");
    }
} 