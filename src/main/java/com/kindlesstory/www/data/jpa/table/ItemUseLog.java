package com.kindlesstory.www.data.jpa.table;

import javax.persistence.TemporalType;
import javax.persistence.Temporal;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Id;
import com.kindlesstory.www.data.jpa.table.id.ItemUseLogId;
import javax.persistence.IdClass;
import javax.persistence.Entity;

@Entity(name = "ITEM_USE_LOG_TB")
@IdClass(ItemUseLogId.class)
public class ItemUseLog
{
    @Id
    @Column(name = "ITEM_REF_CODE")
    private String itemRefCode;
    @Id
    @Column(name = "ITEM_USE_TIME")
    @Temporal(TemporalType.TIMESTAMP)
    private Date itemUseTime;
    @Column(name = "ITEM_USE_RESULT")
    private long itemUseResult;
    
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
    
    public long getItemUseResult() {
        return itemUseResult;
    }
    
    public void setItemUseResult(long itemUseResult) {
        this.itemUseResult = itemUseResult;
    }
}