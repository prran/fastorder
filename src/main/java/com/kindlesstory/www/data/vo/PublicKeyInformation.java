package com.kindlesstory.www.data.vo;

public class PublicKeyInformation
{
    private String publicKeyModulus;
    private String publicKeyExponent;
    private String publicEncode;
    
    public String getPublicKeyModulus() {
        return publicKeyModulus;
    }
    
    public void setPublicKeyModulus(String publicKeyModulus) {
        this.publicKeyModulus = publicKeyModulus;
    }
    
    public String getPublicKeyExponent() {
        return publicKeyExponent;
    }
    
    public void setPublicKeyExponent(String publicKeyExponent) {
        this.publicKeyExponent = publicKeyExponent;
    }
    
    public String getPublicEncode() {
        return publicEncode;
    }
    
    public void setPublicEncode(String publicEncode) {
        this.publicEncode = publicEncode;
    }
}