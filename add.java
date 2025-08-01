import java.util.Scanner;

public class add {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        // 读取输入数组
        String inputLine = scanner.nextLine();
        
        // 解析数组格式 [1,2] -> 提取数字
        String numbersStr = inputLine.replace("[", "").replace("]", "");
        String[] numberStrings = numbersStr.split(",");
        
        // 计算总和
        int sum = 0;
        for (String numStr : numberStrings) {
            sum += Integer.parseInt(numStr.trim());
        }
        
        // 输出结果
        System.out.println(sum);
        
        scanner.close();
    }
}