package com.kindlesstory.www.data.jpa.table;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Entity;
import java.io.Serializable;

@Entity
@Table(name = "ITEM_OWNER_CODE_TB")
public class ItemOwnerCode implements Serializable, JpaTable
{
    private static final long serialVersionUID = 202212271945L;
    @Id
    @Column(name = "ITEM_OWNER_CODE")
    private String itemOwnerCode;
    
    public String getItemOwnerCode() {
        return itemOwnerCode;
    }
    
    public void setItemOwnerCode(final String itemOwnerCode) {
        this.itemOwnerCode = itemOwnerCode;
    }
}