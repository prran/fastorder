package com.kindlesstory.www.data.vo.global;

public class Key
{
    private static final String JWT_KEY = "Boa2_snake0-swallowed2 anElephant3";
    private static final String IV = "Puss_in-bootssas";
    private static final String SALT = "Crocodile-tears";
    private static final String PASSWORD = "Pick of the litter";
    
    private Key() {
    }
    
    public static String getJWTKey() {
        return JWT_KEY;
    }
    
    public static String getIv() {
        return IV;
    }
    
    public static String getSalt() {
        return SALT;
    }
    
    public static String getPassword() {
        return PASSWORD;
    }
}