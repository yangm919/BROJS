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