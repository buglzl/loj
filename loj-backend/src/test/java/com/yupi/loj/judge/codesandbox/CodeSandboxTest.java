package com.yupi.loj.judge.codesandbox;

import com.yupi.loj.judge.codesandbox.impl.ExampleCodeSandBox;
import com.yupi.loj.judge.codesandbox.impl.RemoteCodeSandBox;
import com.yupi.loj.judge.codesandbox.impl.ThridPartyCodeSandBox;
import com.yupi.loj.judge.codesandbox.model.ExecuteCodeRequest;
import com.yupi.loj.judge.codesandbox.model.ExecuteCodeResponse;
import com.yupi.loj.model.enums.QuestionSubmitLanguageEnum;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CodeSandboxTest {
    /**
     * Value 注解解析 application.yml 的沙箱类型参数，默认 example
     * 注意：只有没有 codesandbox.type 的时候，默认才是 example
     */
    @Value("${codesandbox.type:example}")
    private String type;

    @Test
    void executeCode() {
        System.out.println(type);
        CodeSandbox codeSandbox = CodeSandboxFactory.newInstance(type);
        codeSandbox = new CodeSandboxProxy(codeSandbox);
        String language = QuestionSubmitLanguageEnum.JAVA.getValue();
        String code = "import java.util.*;\n" +
                "\n" +
                "\n" +
                "\n" +
                "public class Main {\n" +
                "    static Scanner sc = new Scanner(System.in);\n" +
                "    static int ans = 0;\n" +
                "    public static void main(String[] args) {\n" +
                "        int n = sc.nextInt();\n" +
                "        while (n-- != 0){\n" +
                "            solve();\n" +
                "        }\n" +
                "\n" +
                "    }\n" +
                "    private static void solve(){\n" +
                "        int A = sc.nextInt();\n" +
                "        int B= sc.nextInt();\n" +
                "        int N = sc.nextInt();\n" +
                "\n" +
                "\n" +
                "        int p = Math.min(B / 2, N / 3);//一行中3能放几块\n" +
                "        int left = N - 3 * p;//剩余长度\n" +
                "        if(left <= A){//left/2=一行需要2的块，A/2=一行能放的块 ，简化后 left<=A\n" +
                "            System.out.println(\"YES\");\n" +
                "        }else {\n" +
                "            System.out.println(\"NO\");\n" +
                "        }\n" +
                "    }\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "}";
        List<String> inputList = Arrays.asList("1\n3 2 6\n", "2\n3 2 6\n1 2 6\n");

        ExecuteCodeRequest executeCodeRequest = ExecuteCodeRequest.builder()
                .code(code)
                .inputList(inputList)
                .language(language)
                .build();

        ExecuteCodeResponse executeCodeResponse = codeSandbox.executeCode(executeCodeRequest);
        Assertions.assertNotNull(executeCodeResponse);
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        while (sc.hasNext()) {
            String type = sc.next();
            CodeSandbox codeSandbox = CodeSandboxFactory.newInstance(type);
            String language = QuestionSubmitLanguageEnum.JAVA.getValue();
            String code = "public class Main";
            List<String> inputList = Arrays.asList("1 2", "3");

            ExecuteCodeRequest executeCodeRequest = ExecuteCodeRequest.builder()
                    .code(code)
                    .inputList(inputList)
                    .language(language)
                    .build();

            ExecuteCodeResponse executeCodeResponse = codeSandbox.executeCode(executeCodeRequest);
//            Assertions.assertNotNull(executeCodeResponse);
        }
    }

}