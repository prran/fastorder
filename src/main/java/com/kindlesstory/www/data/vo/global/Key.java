package com.kindlesstory.www.data.vo.global;

public class Key
{
    private static final String JWT_KEY = "Boa2_snake0-swallowed2 anElephant3",
    		IV = "Puss_in-bootssas",
    		SALT = "Crocodile-tears",
    		PASSWORD = "Pick of the litter";
    
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