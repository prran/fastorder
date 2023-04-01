package com.kindlesstory.www.data.jpa.table;

import javax.persistence.TemporalType;
import javax.persistence.Temporal;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Id;
import com.kindlesstory.www.data.jpa.table.id.ItemId;
import javax.persistence.IdClass;
import javax.persistence.Table;
import javax.persistence.Entity;
import java.io.Serializable;

@Entity
@Table(name = "ITEM_TB")
@IdClass(ItemId.class)
public class Item implements Serializable, JpaTable
{
    private static final long serialVersionUID = 202212271939L;
    @Id
    @Column(name = "KATE_NAME")
    private String kateName;
    @Id
    @Column(name = "ITEM_NAME")
    private String itemName;
    @Column(name = "ITEM_REF_CODE")
    private String itemRefCode;
    @Column(name = "ITEM_OWNER_CODE")
    private String itemOwnerCode;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "ITEM_AGE")
    private Date itemAge;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "ITEM_RECENT")
    private Date itemRecent;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "ITEM_DEATH")
    private Date itemDeath;
    @Column(name = "ITEM_PASSWORD")
    private String itemPassword;
    
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
    
    public String getItemRefCode() {
        return itemRefCode;
    }
    
    public void setItemRefCode(final String itemRefCode) {
        this.itemRefCode = itemRefCode;
    }
    
    public String getItemOwnerCode() {
        return itemOwnerCode;
    }
    
    public void setItemOwnerCode(final String itemOwnerCode) {
        this.itemOwnerCode = itemOwnerCode;
    }
    
    public Date getItemAge() {
        return itemAge;
    }
    
    public void setItemAge(final Date itemAge) {
        this.itemAge = itemAge;
    }
    
    public Date getItemRecent() {
        return itemRecent;
    }
    
    public void setItemRecent(final Date itemRecent) {
        this.itemRecent = itemRecent;
    }
    
    public Date getItemDeath() {
        return itemDeath;
    }
    
    public void setItemDeath(final Date itemDeath) {
        this.itemDeath = itemDeath;
    }
    
    public String getItemPassword() {
        return itemPassword;
    }
    
    public void setItemPassword(final String itemPassword) {
        this.itemPassword = itemPassword;
    }
}