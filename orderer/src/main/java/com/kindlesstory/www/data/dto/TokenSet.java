package com.kindlesstory.www.data.dto;

public class TokenSet
{
    private String accessToken;
    private String refreshToken;
    
    public String getAccessToken() {
        return accessToken;
    }
    
    public void setAccessToken(final String accessToken) {
        this.accessToken = accessToken;
    }
    
    public String getRefreshToken() {
        return refreshToken;
    }
    
    public void setRefreshToken(final String refreshToken) {
        this.refreshToken = refreshToken;
    }
}