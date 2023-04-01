package com.kindlesstory.www.data.jpa.table;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Entity;
import java.io.Serializable;

@Entity(name = "ITEM_REF_CODE_TB")
public class ItemRefCode implements Serializable, JpaTable
{
    private static final long serialVersionUID = 202212271941L;
    @Id
    @Column(name = "ITEM_REF_CODE")
    private String itemRefCode;
    
    public String getItemRefCode() {
        return itemRefCode;
    }
    
    public void setItemRefCode(final String itemRefCode) {
        this.itemRefCode = itemRefCode;
    }
}