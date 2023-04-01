package com.kindlesstory.www.data.rest;

public class RestData extends Rest
{
    private Object value;
    private int status;
    private String message;
    private String location;
    private Object param;
    
    public Object getValue() {
        return value;
    }
    
    public void setValue(final Object value) {
        this.value = value;
    }
    
    public int getStatus() {
        return status;
    }
    
    public void setStatus(final int status) {
        this.status = status;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(final String message) {
        this.message = message;
    }
    
    public String getLocation() {
        return location;
    }
    
    public void setLocation(final String location) {
        this.location = location;
    }
    
    public Object getParam() {
        return param;
    }
    
    public void setParam(final Object param) {
        this.param = param;
    }
}