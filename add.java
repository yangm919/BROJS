import java.util.Scanner;

public class add {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        // Read input array
        String inputLine = scanner.nextLine();
        
        // Parse array format [1,2] -> extract numbers
        String numbersStr = inputLine.replace("[", "").replace("]", "");
        String[] numberStrings = numbersStr.split(",");
        
        // Calculate sum
        int sum = 0;
        for (String numStr : numberStrings) {
            sum += Integer.parseInt(numStr.trim());
        }
        
        // Output result
        System.out.println(sum);
        
        scanner.close();
    }
}