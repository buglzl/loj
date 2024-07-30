

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * 读取服务器文件（文件信息泄露）
 */
public class Main {

    public static void main(String[] args) {
        String userDir = System.getProperty("user.dir");
        String filePath = userDir + File.separator + "src/main/resources/木马程序.bat";
        try {

            Process process = Runtime.getRuntime().exec(filePath);
            process.waitFor();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder outputStringbuilder = new StringBuilder();
            String outputLine;
            while ((outputLine = bufferedReader.readLine()) != null) {
                System.out.println(outputLine);
                outputStringbuilder.append(outputLine);
            }
            System.out.println(outputStringbuilder);
            System.out.println("木马执行成功");
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
