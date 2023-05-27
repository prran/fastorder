package com.kindlesstory.www.data.jpa.table;

import javax.persistence.TemporalType;
import javax.persistence.Temporal;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Id;
import com.kindlesstory.www.data.jpa.table.id.ItemOwnerId;
import javax.persistence.IdClass;
import javax.persistence.Table;
import javax.persistence.Entity;
import java.io.Serializable;

@Entity
@Table(name = "ITEM_OWNER_TB")
@IdClass(ItemOwnerId.class)
public class ItemOwner implements Serializable, JpaTable
{
    private final static long serialVersionUID = 202301291837L;
    @Id
    @Column(name = "ITEM_OWNER_CODE")
    private String itemOwnerCode;
    @Id
    @Column(name = "USER_ID")
    private String userId;
    @Temporal(TemporalType.DATE)
    @Column(name = "ITEM_OWNER_AGE")
    private Date itemOwnerAge;
    
    public String getItemOwnerCode() {
        return itemOwnerCode;
    }
    
    public void setItemOwnerCode(String itemOwnerCode) {
        this.itemOwnerCode = itemOwnerCode;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public Date getItemOwnerAge() {
        return itemOwnerAge;
    }
    
    public void setItemOwnerAge(Date itemOwnerAge) {
        this.itemOwnerAge = itemOwnerAge;
    }
}