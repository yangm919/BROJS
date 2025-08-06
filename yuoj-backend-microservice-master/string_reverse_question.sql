-- Add String Reverse question
INSERT INTO `question` (
    `title`, 
    `content`, 
    `tags`, 
    `answer`, 
    `judgeCase`, 
    `judgeConfig`, 
    `userId`
) VALUES (
    'String Reverse',
    'Given a string, reverse the string and return the reversed string.

Example 1:
Input: "hello"
Output: "olleh"

Example 2:
Input: "world"
Output: "dlrow"

Example 3:
Input: "algorithm"
Output: "mhtirogla"

Constraints:
1 <= s.length <= 100
s consists of printable ASCII characters

Note: You should not use any built-in string reverse functions. Implement the reverse logic yourself.',
    '["String", "Two Pointers", "Basic Algorithm"]',
    '```java
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        // Read input string
        String s = scanner.nextLine();
        
        // Reverse the string
        String reversed = reverseString(s);
        
        // Output result
        System.out.println(reversed);
        
        scanner.close();
    }
    
    public static String reverseString(String s) {
        if (s == null || s.length() <= 1) {
            return s;
        }
        
        char[] chars = s.toCharArray();
        int left = 0;
        int right = chars.length - 1;
        
        // Use two pointers to reverse the string
        while (left < right) {
            // Swap characters at left and right positions
            char temp = chars[left];
            chars[left] = chars[right];
            chars[right] = temp;
            
            left++;
            right--;
        }
        
        return new String(chars);
    }
}
```',
    '[{"input": "hello", "output": "olleh"}, {"input": "world", "output": "dlrow"}, {"input": "algorithm", "output": "mhtirogla"}, {"input": "a", "output": "a"}, {"input": "ab", "output": "ba"}, {"input": "racecar", "output": "racecar"}]',
    '{"timeLimit": 1000, "memoryLimit": 256000}',
    1
); 