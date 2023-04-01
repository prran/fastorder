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
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public String getKateName() {
        return kateName;
    }
    
    public void setKateName(final String kateName) {
        this.kateName = kateName;
    }
    
    public String getCode() {
        return code;
    }
    
    public void setCode(final String code) {
        this.code = code;
    }
    
    public boolean getItemDeath() {
        return itemDeath;
    }
    
    public void setItemDeath(final boolean itemDeath) {
        this.itemDeath = itemDeath;
    }
}