package com.kindlesstory.www.module;

import java.util.List;

public class Tester
{
    public static void printStringArray(final List<String> stringList) {
        for (final String string : stringList) {
            System.out.println(string);
        }
    }
    
    public static void flag(final int lineNum) {
        System.err.println("execute location : " + lineNum);
    }
}