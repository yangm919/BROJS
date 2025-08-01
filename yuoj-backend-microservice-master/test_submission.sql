-- 添加测试提交
INSERT INTO `question_submit` (
    `language`, 
    `code`, 
    `questionId`, 
    `userId`, 
    `status`
) VALUES (
    'java',
    'import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String inputLine = scanner.nextLine();
        String numbersStr = inputLine.replace("[", "").replace("]", "");
        String[] numberStrings = numbersStr.split(",");
        int sum = 0;
        for (String numStr : numberStrings) {
            sum += Integer.parseInt(numStr.trim());
        }
        System.out.println(sum);
        scanner.close();
    }
}',
    2,
    1,
    0
); 