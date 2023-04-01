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
    List<Item> findByKateNameOrderByItemAgeDesc(final String kategorie, final Pageable pg);
    
    Item findByItemRefCode(final String itemCode);
    
    Long countByItemAgeGreaterThanAndKateNameIs(final Date age, final String kategorie);
    
    List<Item> findByItemAgeLessThanAndKateNameIsOrderByItemAgeDesc(final Date age, final String kategorie, final Pageable pg);
    
    List<Item> findByItemAgeLessThanAndKateNameIsAndItemNameContainingOrderByItemAgeDesc(final Date age, final String kategorie, final String name, final Pageable pg);
    
    boolean existsByItemRefCode(final String itemCode);
    
    List<Item> findByItemDeathLessThan(final Date death);
    
    List<Item> findByItemNameContainingAndKateNameIsOrderByItemAgeDesc(final String name, final String kategorie, final Pageable pg);
    
    @Query("SELECT i FROM Item i INNER JOIN ItemOwner o ON i.itemOwnerCode = o.itemOwnerCode WHERE o.userId = :id")
    List<Item> findById(@Param("id") final String id);
}