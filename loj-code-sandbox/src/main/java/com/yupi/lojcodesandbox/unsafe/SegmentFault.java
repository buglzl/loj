package com.yupi.lojcodesandbox.unsafe;

import java.util.Scanner;

public class SegmentFault {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int n = sc.nextInt();
        int time = 0;
        System.out.printf("%.3f\n", (double)n / time);
    }
}
