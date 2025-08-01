import java.util.Arrays;

/**
 * 两数之和 - 双指针解法（适用于已排序数组）
 * 时间复杂度: O(n log n) - 排序时间
 * 空间复杂度: O(n) - 需要存储原始索引
 */
public class TwoSumTwoPointers {
    
    public int[] twoSum(int[] nums, int target) {
        // 创建索引数组，用于保存原始位置
        int[] indices = new int[nums.length];
        for (int i = 0; i < nums.length; i++) {
            indices[i] = i;
        }
        
        // 对数组进行排序，同时保持索引对应关系
        sortWithIndices(nums, indices);
        
        // 双指针查找
        int left = 0;
        int right = nums.length - 1;
        
        while (left < right) {
            int sum = nums[left] + nums[right];
            
            if (sum == target) {
                // 返回原始索引
                return new int[]{indices[left], indices[right]};
            } else if (sum < target) {
                left++;
            } else {
                right--;
            }
        }
        
        return new int[]{};
    }
    
    // 排序方法，同时保持索引对应关系
    private void sortWithIndices(int[] nums, int[] indices) {
        // 使用冒泡排序保持索引对应关系
        for (int i = 0; i < nums.length - 1; i++) {
            for (int j = 0; j < nums.length - 1 - i; j++) {
                if (nums[j] > nums[j + 1]) {
                    // 交换数值
                    int temp = nums[j];
                    nums[j] = nums[j + 1];
                    nums[j + 1] = temp;
                    
                    // 交换索引
                    int tempIndex = indices[j];
                    indices[j] = indices[j + 1];
                    indices[j + 1] = tempIndex;
                }
            }
        }
    }
    
    // 测试方法
    public static void main(String[] args) {
        TwoSumTwoPointers solution = new TwoSumTwoPointers();
        
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