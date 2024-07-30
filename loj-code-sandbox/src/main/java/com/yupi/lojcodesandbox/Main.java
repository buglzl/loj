package com.yupi.lojcodesandbox;

import java.io.*;
import java.util.*;
import java.math.*;
public class Main {
    static BufferedReader sc = new BufferedReader(new InputStreamReader(System.in));
    static PrintWriter pw = new PrintWriter(new OutputStreamWriter(System.out));
    public static void main(String[] args) throws IOException {
        int T = 1;T = Integer.valueOf(sc.readLine());
        while (T-- > 0) {
            String[] s = sc.readLine().split(" ");
            long a = Long.valueOf(s[0]),b = Long.valueOf(s[1]),n = Long.valueOf(s[2]);
            if(2 * a + 3 * b < 2 * n){
                pw.println("NO");
            }else{
                long A = (b / 2) * 3;
                long R = n - Math.min(n / 3 * 3,A);
                if(a >= R){
                    pw.println("YES");
                }else {
                    pw.println("NO");
                }

            }
        }
        pw.flush();
    }
}