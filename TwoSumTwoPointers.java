import java.util.Arrays;

/**
 * Two Sum - Two Pointers Solution (for sorted arrays)
 * Time Complexity: O(n log n) - sorting time
 * Space Complexity: O(n) - need to store original indices
 */
public class TwoSumTwoPointers {
    
    public int[] twoSum(int[] nums, int target) {
        // Create index array to save original positions
        int[] indices = new int[nums.length];
        for (int i = 0; i < nums.length; i++) {
            indices[i] = i;
        }
        
        // Sort array while maintaining index correspondence
        sortWithIndices(nums, indices);
        
        // Two pointers search
        int left = 0;
        int right = nums.length - 1;
        
        while (left < right) {
            int sum = nums[left] + nums[right];
            
            if (sum == target) {
                // Return original indices
                return new int[]{indices[left], indices[right]};
            } else if (sum < target) {
                left++;
            } else {
                right--;
            }
        }
        
        return new int[]{};
    }
    
    // Sorting method while maintaining index correspondence
    private void sortWithIndices(int[] nums, int[] indices) {
        // Use bubble sort to maintain index correspondence
        for (int i = 0; i < nums.length - 1; i++) {
            for (int j = 0; j < nums.length - 1 - i; j++) {
                if (nums[j] > nums[j + 1]) {
                    // Swap values
                    int temp = nums[j];
                    nums[j] = nums[j + 1];
                    nums[j + 1] = temp;
                    
                    // Swap indices
                    int tempIndex = indices[j];
                    indices[j] = indices[j + 1];
                    indices[j + 1] = tempIndex;
                }
            }
        }
    }
    
    // Test method
    public static void main(String[] args) {
        TwoSumTwoPointers solution = new TwoSumTwoPointers();
        
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