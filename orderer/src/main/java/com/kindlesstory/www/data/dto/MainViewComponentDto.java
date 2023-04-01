package com.kindlesstory.www.data.dto;

import java.util.List;

public class MainViewComponentDto
{
    private String kategorie;
    private List<SimpleItem> items;
    
    public String getKategorie() {
        return kategorie;
    }
    
    public void setKategorie(final String kategorie) {
        this.kategorie = kategorie;
    }
    
    public List<SimpleItem> getItems() {
        return items;
    }
    
    public void setItems(final List<SimpleItem> items) {
        this.items = items;
    }
}