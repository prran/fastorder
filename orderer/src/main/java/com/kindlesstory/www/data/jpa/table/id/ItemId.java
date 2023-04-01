package com.kindlesstory.www.data.jpa.table.id;

import java.util.Objects;
import java.io.Serializable;

public class ItemId implements Serializable
{
    private static final long serialVersionUID = 202212270939L;
    private String kateName;
    private String itemName;
    
    public String getKateName() {
        return kateName;
    }
    
    public void setKateName(final String kateName) {
        this.kateName = kateName;
    }
    
    public String getItemName() {
        return itemName;
    }
    
    public void setItemName(final String itemName) {
        this.itemName = itemName;
    }
    
    @Override
    public boolean equals(final Object o) {
        try {
            final ItemId id = (ItemId)o;
            return id.getItemName().equals(itemName) && id.getKateName().equals(kateName);
        }
        catch (NullPointerException | ClassCastException ex2) {
            return false;
        }
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(itemName, kateName);
    }
}
