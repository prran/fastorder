package com.kindlesstory.www.data.vo;

public class PublicKeyInformation
{
    private String publicKeyModulus;
    private String publicKeyExponent;
    private String publicEncode;
    
    public String getPublicKeyModulus() {
        return publicKeyModulus;
    }
    
    public void setPublicKeyModulus(final String publicKeyModulus) {
        this.publicKeyModulus = publicKeyModulus;
    }
    
    public String getPublicKeyExponent() {
        return publicKeyExponent;
    }
    
    public void setPublicKeyExponent(final String publicKeyExponent) {
        this.publicKeyExponent = publicKeyExponent;
    }
    
    public String getPublicEncode() {
        return publicEncode;
    }
    
    public void setPublicEncode(final String publicEncode) {
        this.publicEncode = publicEncode;
    }
}