package com.kindlesstory.www.data.jpa.table;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Entity;
import java.io.Serializable;

@Entity(name = "SORT_ITEM_TB")
public class SortItem implements Serializable, JpaTable
{
    private static final long serialVersionUID = 202301281739L;
    @Id
    @Column(name = "ITEM_REF_CODE")
    private String itemRefCode;
    @Column(name = "SORT_ITEM_CONTEXT")
    private String sortItemContext;
    @Column(name = "SORT_ITEM_LENGTH")
    private int sortItemLength;
    
    public String getItemRefCode() {
        return itemRefCode;
    }
    
    public void setItemRefCode(final String itemRefCode) {
        this.itemRefCode = itemRefCode;
    }
    
    public String getSortItemContext() {
        return sortItemContext;
    }
    
    public void setSortItemContext(final String sortItemContext) {
        this.sortItemContext = sortItemContext;
    }
    
    public int getSortItemLength() {
        return sortItemLength;
    }
    
    public void setSortItemLength(final int i) {
        this.sortItemLength = i;
    }
}