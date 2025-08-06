/**
 * Two Sum - Brute Force Solution
 * Time Complexity: O(n²)
 * Space Complexity: O(1)
 */
public class TwoSumBruteForce {
    
    public int[] twoSum(int[] nums, int target) {
        // Double loop to traverse all possible combinations
        for (int i = 0; i < nums.length; i++) {
            for (int j = i + 1; j < nums.length; j++) {
                // If we find two numbers that sum to target
                if (nums[i] + nums[j] == target) {
                    return new int[]{i, j};
                }
            }
        }
        
        // If no answer is found, return empty array
        return new int[]{};
    }
    
    // Test method
    public static void main(String[] args) {
        TwoSumBruteForce solution = new TwoSumBruteForce();
        
        // Test case 1
        int[] nums1 = {2, 7, 11, 15};
        int target1 = 9;
        int[] result1 = solution.twoSum(nums1, target1);
        System.out.println("Test case 1: nums = [2,7,11,15], target = 9");
        System.out.println("Result: [" + result1[0] + "," + result1[1] + "]");
        
        // Test case 2
        int[] nums2 = {3, 2, 4};
        int target2 = 6;
        int[] result2 = solution.twoSum(nums2, target2);
        System.out.println("Test case 2: nums = [3,2,4], target = 6");
        System.out.println("Result: [" + result2[0] + "," + result2[1] + "]");
        
        // Test case 3
        int[] nums3 = {3, 3};
        int target3 = 6;
        int[] result3 = solution.twoSum(nums3, target3);
        System.out.println("Test case 3: nums = [3,3], target = 6");
        System.out.println("Result: [" + result3[0] + "," + result3[1] + "]");
    }
} 