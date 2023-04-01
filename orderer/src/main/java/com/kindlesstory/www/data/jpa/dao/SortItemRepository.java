package com.kindlesstory.www.data.jpa.dao;

import com.kindlesstory.www.data.jpa.table.SortItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SortItemRepository extends JpaRepository<SortItem, String>
{
}