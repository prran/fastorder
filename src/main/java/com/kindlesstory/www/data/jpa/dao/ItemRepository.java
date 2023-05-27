package com.kindlesstory.www.data.jpa.dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Date;
import java.util.List;
import org.springframework.data.domain.Pageable;
import com.kindlesstory.www.data.jpa.table.id.ItemId;
import com.kindlesstory.www.data.jpa.table.Item;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item, ItemId>
{
    List<Item> findByKateNameOrderByItemAgeDesc(String kategorie, Pageable pg);
    
    Item findByItemRefCode(String itemCode);
    
    Long countByItemAgeGreaterThanAndKateNameIs(Date age, String kategorie);
    
    List<Item> findByItemAgeLessThanAndKateNameIsOrderByItemAgeDesc(Date age, String kategorie, Pageable pg);
    
    List<Item> findByItemAgeLessThanAndKateNameIsAndItemNameContainingOrderByItemAgeDesc(Date age, String kategorie, String name, Pageable pg);
    
    boolean existsByItemRefCode(String itemCode);
    
    List<Item> findByItemDeathLessThan(Date death);
    
    List<Item> findByItemNameContainingAndKateNameIsOrderByItemAgeDesc(String name, String kategorie, Pageable pg);
    
    @Query("SELECT i FROM Item i INNER JOIN ItemOwner o ON i.itemOwnerCode = o.itemOwnerCode WHERE o.userId = :id")
    List<Item> findById(@Param("id") String id);
}