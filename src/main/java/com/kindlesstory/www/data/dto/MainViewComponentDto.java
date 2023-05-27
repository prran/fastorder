package com.kindlesstory.www.data.dto;

import java.util.List;

public class MainViewComponentDto
{
    private String kategorie;
    private List<SimpleItem> items;
    
    public String getKategorie() {
        return kategorie;
    }
    
    public void setKategorie(String kategorie) {
        this.kategorie = kategorie;
    }
    
    public List<SimpleItem> getItems() {
        return items;
    }
    
    public void setItems(List<SimpleItem> items) {
        this.items = items;
    }
}