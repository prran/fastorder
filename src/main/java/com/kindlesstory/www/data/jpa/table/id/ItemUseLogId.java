package com.kindlesstory.www.data.jpa.table.id;

import java.util.Objects;
import java.util.Date;
import java.io.Serializable;

public class ItemUseLogId implements Serializable
{
    private final static long serialVersionUID = 202301281824L;
    private String itemRefCode;
    private Date itemUseTime;
    
    public String getItemRefCode() {
        return itemRefCode;
    }
    
    public void setItemRefCode(String itemRefCode) {
        this.itemRefCode = itemRefCode;
    }
    
    public Date getItemUseTime() {
        return itemUseTime;
    }
    
    public void setItemUseTime(Date itemUseTime) {
        this.itemUseTime = itemUseTime;
    }
    
    @Override
    public boolean equals(Object o) {
        try {
            ItemUseLogId id = (ItemUseLogId)o;
            return id.getItemRefCode().equals(itemRefCode) && id.getItemUseTime().equals(itemUseTime);
        }
        catch (NullPointerException | ClassCastException ex2) {
            return false;
        }
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(itemRefCode, itemUseTime);
    }
}