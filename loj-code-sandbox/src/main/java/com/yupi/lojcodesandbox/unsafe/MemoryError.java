package com.yupi.lojcodesandbox.unsafe;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * 无限占用空间（浪费系统内存）
 */
public class MemoryError {
    static final int N = 100000;
    public static void main(String[] args) throws InterruptedException {
        Scanner sc = new Scanner(System.in);
        int n = sc.nextInt();
        int[] a = new int[n];
        for (int i = 0; i < n; i ++ ) a[i] = i;
        System.out.println("内存不爆炸");
    }
}
