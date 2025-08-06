package com.yupi.yuojcodesandbox.unsafe;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Run other programs (such as dangerous trojans)
 */
public class RunFileError {

    public static void main(String[] args) throws InterruptedException, IOException {
        String userDir = System.getProperty("user.dir");
        String filePath = userDir + File.separator + "src/main/resources/trojan.bat";
        Process process = Runtime.getRuntime().exec(filePath);
        process.waitFor();
        // Get process normal output in batches
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        // Read line by line
        String compileOutputLine;
        while ((compileOutputLine = bufferedReader.readLine()) != null) {
            System.out.println(compileOutputLine);
        }
        System.out.println("Successfully executed malicious program");
    }
}
