package com.kindlesstory.www.data.dto;

public class SimpleItem
{
    private String name;
    private String kateName;
    private String code;
    private boolean itemDeath;
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getKateName() {
        return kateName;
    }
    
    public void setKateName(String kateName) {
        this.kateName = kateName;
    }
    
    public String getCode() {
        return code;
    }
    
    public void setCode(String code) {
        this.code = code;
    }
    
    public boolean getItemDeath() {
        return itemDeath;
    }
    
    public void setItemDeath(boolean itemDeath) {
        this.itemDeath = itemDeath;
    }
}