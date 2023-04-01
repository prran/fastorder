package com.kindlesstory.www.data.jpa.table.id;

import java.util.Objects;
import java.util.Date;
import java.io.Serializable;

public class ItemUseLogId implements Serializable
{
    private static final long serialVersionUID = 202301281824L;
    private String itemRefCode;
    private Date itemUseTime;
    
    public String getItemRefCode() {
        return itemRefCode;
    }
    
    public void setItemRefCode(final String itemRefCode) {
        this.itemRefCode = itemRefCode;
    }
    
    public Date getItemUseTime() {
        return itemUseTime;
    }
    
    public void setItemUseTime(final Date itemUseTime) {
        this.itemUseTime = itemUseTime;
    }
    
    @Override
    public boolean equals(final Object o) {
        try {
            final ItemUseLogId id = (ItemUseLogId)o;
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