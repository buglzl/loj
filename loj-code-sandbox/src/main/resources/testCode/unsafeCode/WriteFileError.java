

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

/**
 * 读取服务器文件（文件信息泄露）
 */
public class Main {
    public static void main(String[] args) {
        String userDir = System.getProperty("user.dir");
        String filePath = userDir + File.separator + "src/main/resources/木马程序.bat";
        String errorProgram = "java -version 2>&1";
        try {
            Files.write(Paths.get(filePath), Arrays.asList(errorProgram));
            System.out.println("植入木马程序");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
