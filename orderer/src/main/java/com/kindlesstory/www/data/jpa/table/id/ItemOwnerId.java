package com.kindlesstory.www.data.jpa.table.id;

import java.util.Objects;
import java.io.Serializable;

public class ItemOwnerId implements Serializable
{
    private static final long serialVersionUID = 202212270853L;
    private String itemOwnerCode;
    private String userId;
    
    public String getItemOwnerCode() {
        return itemOwnerCode;
    }
    
    public void setItemOwnerCode(final String itemOwnerCode) {
        this.itemOwnerCode = itemOwnerCode;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(final String userId) {
        this.userId = userId;
    }
    
    @Override
    public boolean equals(final Object o) {
        try {
            final ItemOwnerId id = (ItemOwnerId)o;
            return id.getItemOwnerCode().equals(itemOwnerCode) && id.getUserId().equals(userId);
        }
        catch (NullPointerException | ClassCastException ex2) {
            return false;
        }
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(itemOwnerCode, userId);
    }
}