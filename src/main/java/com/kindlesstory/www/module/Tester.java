package com.kindlesstory.www.module;

import java.util.List;

public class Tester
{
    public static void printStringArray(List<String> stringList) {
        for (String string : stringList) {
            System.out.println(string);
        }
    }
    
    public static void flag(int lineNum) {
        System.err.println("execute location : " + lineNum);
    }
}