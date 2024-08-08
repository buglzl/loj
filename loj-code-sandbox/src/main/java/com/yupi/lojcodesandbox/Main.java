package com.yupi.lojcodesandbox;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int n = sc.nextInt(); // 可以修改 n 的值进行测试
        List<List<Integer>> result = permute(n);
        for (List<Integer> permutation : result) {
            System.out.println(permutation);
        }
    }

    public static List<List<Integer>> permute(int n) {
        List<List<Integer>> result = new ArrayList<>();
        List<Integer> nums = new ArrayList<>();
        for (int i = 1; i <= n; i++) {
            nums.add(i);
        }
        backtrack(result, new ArrayList<>(), nums);
        return result;
    }

    private static void backtrack(List<List<Integer>> result, List<Integer> tempList, List<Integer> nums) {
        if (tempList.size() == nums.size()) {
            result.add(new ArrayList<>(tempList));
        } else {
            for (int i = 0; i < nums.size(); i++) {
                if (tempList.contains(nums.get(i))) continue; // 已经包含的元素跳过
                tempList.add(nums.get(i));
                backtrack(result, tempList, nums);
                tempList.remove(tempList.size() - 1);
            }
        }
    }
}
