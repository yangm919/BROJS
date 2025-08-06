import java.util.HashMap;
import java.util.Map;

/**
 * Two Sum - Hash Table Solution
 * Time Complexity: O(n)
 * Space Complexity: O(n)
 */
public class TwoSumSolution {
    
    public int[] twoSum(int[] nums, int target) {
        // Create hash table to store number and index mapping
        Map<Integer, Integer> map = new HashMap<>();
        
        // Traverse array
        for (int i = 0; i < nums.length; i++) {
            // Calculate complement needed for current number
            int complement = target - nums[i];
            
            // If complement exists in hash table, we found the answer
            if (map.containsKey(complement)) {
                return new int[]{map.get(complement), i};
            }
            
            // Add current number and index to hash table
            map.put(nums[i], i);
        }
        
        // If no answer is found, return empty array
        return new int[]{};
    }
    
    // Test method
    public static void main(String[] args) {
        TwoSumSolution solution = new TwoSumSolution();
        
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