# Python solution for Maximum Subarray problem
def max_sub_array(nums):
    """
    Kadane's algorithm to find maximum subarray sum
    """
    if not nums:
        return 0
    
    max_sum = nums[0]
    current_sum = nums[0]
    
    for i in range(1, len(nums)):
        current_sum = max(nums[i], current_sum + nums[i])
        max_sum = max(max_sum, current_sum)
    
    return max_sum

# Test cases
if __name__ == "__main__":
    # Read input from stdin
    import sys
    input_line = sys.stdin.readline().strip()
    
    # Parse input: remove brackets and split by comma
    numbers_str = input_line.replace("[", "").replace("]", "")
    number_strings = numbers_str.split(",")
    
    # Convert to integers
    nums = [int(num.strip()) for num in number_strings]
    
    # Calculate and print result
    result = max_sub_array(nums)
    print(result) 