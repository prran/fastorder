package com.kindlesstory.www.data.dto;

import java.util.List;

public class OrderExecuteMetadata
{
    private String orderContext;
    private String itemName;
    private int rank;
    private List<Long> itemUseResultListTop5;
    private List<String> userNameList;
    
    public String getOrderContext() {
        return orderContext;
    }
    
    public void setOrderContext(String orderContext) {
        this.orderContext = orderContext;
    }
    
    public String getItemName() {
        return itemName;
    }
    
    public void setItemName(String itemName) {
        this.itemName = itemName;
    }
    
    public List<Long> getItemUseResultListTop5() {
        return itemUseResultListTop5;
    }
    
    public void setItemUseResultListTop5(List<Long> itemUseResultListTop5) {
        this.itemUseResultListTop5 = itemUseResultListTop5;
    }
    
    public List<String> getUserNameList() {
        return userNameList;
    }
    
    public void setUserNameList(List<String> userNameList) {
        this.userNameList = userNameList;
    }
    
    public int getRank() {
        return rank;
    }
    
    public void setRank(int rank) {
        this.rank = rank;
    }
}