import java.util.HashMap;
import java.util.Map;

/**
 * 两数之和 - 哈希表解法
 * 时间复杂度: O(n)
 * 空间复杂度: O(n)
 */
public class TwoSumSolution {
    
    public int[] twoSum(int[] nums, int target) {
        // 创建哈希表存储数字和索引的映射
        Map<Integer, Integer> map = new HashMap<>();
        
        // 遍历数组
        for (int i = 0; i < nums.length; i++) {
            // 计算当前数字需要的补数
            int complement = target - nums[i];
            
            // 如果补数在哈希表中存在，说明找到了答案
            if (map.containsKey(complement)) {
                return new int[]{map.get(complement), i};
            }
            
            // 将当前数字和索引加入哈希表
            map.put(nums[i], i);
        }
        
        // 如果没有找到答案，返回空数组
        return new int[]{};
    }
    
    // 测试方法
    public static void main(String[] args) {
        TwoSumSolution solution = new TwoSumSolution();
        
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