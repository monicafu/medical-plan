package com.medical.plan.demo.Tools;

import java.util.*;

public class Test {
    public static List<String> solution(String t, String s) {
        List<String> res = new ArrayList<String>();

        String[] tArray = t.split(" ");
        String[] sArray = s.split((" "));

        int j = 0;
        for(int i = 0; i < sArray.length; i++) {
            while(!sArray[i].equals(tArray[j])) {
                res.add(tArray[j]);
                j++;
            }
            j++;
        }

        while(j < tArray.length) {
            res.add(tArray[j]);
            j++;
        }
        return res;
    }

    public static List<String> solution3(String s, String t) {
        List<String> res = new ArrayList<String>();

        if(t == null || t.length() == 0) {
            return Arrays.asList(s.split(" "));
        }
        String[] tArray = t.split(" ");
        String[] sArray = s.split((" "));

        for(int i = 0, j =0; i < sArray.length; i++) {
            if(!sArray[i].equals(tArray[j])) {
                res.add(sArray[i]);
            } else {
                j++;
            }
        }

        return res;
    }

    public static void solution(String str) {

        int leftCon = 0;
        int rightCon = 0;

        List<String> res = new ArrayList<String>();

        //find the index of last consonant
        for(int i = str.length() - 1; i >= 0; i--) {
            if(!isVol(str.charAt(i))) {
                rightCon = i;
                break;
            }
        }

        for(int i = 0; i <rightCon; i++) {
            if(isVol(str.charAt(i))) {
                if(i >= leftCon) {
                    while(leftCon < rightCon && isVol(str.charAt(leftCon))) {
                        leftCon++;
                    }
                }

                res.add(str.substring(i, leftCon + 1));
                res.add(str.substring(i, rightCon + 1));
            }
        }

        Collections.sort(res);

        System.out.println(res.get(0));
        System.out.println(res.get(res.size() - 1));
    }

    private static boolean isVol(char c) {
        return c == 'a' || c == 'e' || c == 'i' || c =='o' || c =='u';
    }

    public static void main(String[] args) {
        Test.solution("abc");
    }
}
