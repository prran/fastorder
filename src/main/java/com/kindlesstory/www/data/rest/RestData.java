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
    
    public void setValue(Object value) {
        this.value = value;
    }
    
    public int getStatus() {
        return status;
    }
    
    public void setStatus(int status) {
        this.status = status;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public String getLocation() {
        return location;
    }
    
    public void setLocation(String location) {
        this.location = location;
    }
    
    public Object getParam() {
        return param;
    }
    
    public void setParam(Object param) {
        this.param = param;
    }
}