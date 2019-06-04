package com.medical.plan.demo.Tools;
import java.security.SecureRandom;
import java.util.Random;

public class UUIDGenerator {
    private static final  String ALPHABET = "abcdefghijklmnopqrstuvwxyz0123456789";
    private static Random rng = new SecureRandom();

    public static char randomChar(){
        return ALPHABET.charAt(rng.nextInt(ALPHABET.length()));
    }

    public static String randomUUID(int length){
        StringBuilder sb = new StringBuilder();
        while(length > 0){
            length--;
            sb.append(randomChar());
        }
        return sb.toString();
    }
    public static void main(String[] args){
        System.out.println(randomUUID(16));
    }
}
