package com.kindlesstory.www.data.jpa.dao;

import java.util.Date;
import java.util.List;
import com.kindlesstory.www.data.jpa.table.id.ItemUseLogId;
import com.kindlesstory.www.data.jpa.table.ItemUseLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemUseLogRepository extends JpaRepository<ItemUseLog, ItemUseLogId>
{
    List<ItemUseLog> findByItemRefCode(String itemCode);
    
    ItemUseLog findTop1ByItemUseResultAndItemRefCodeOrderByItemUseTimeDesc(Long resultTime, String itemCode);
    
    List<ItemUseLog> findTop5ByItemUseTimeGreaterThanAndItemRefCodeOrderByItemUseResultAsc(Date useTime, String itemCode);
    
    List<ItemUseLog> findTop5ByItemUseTimeLessThanAndItemRefCodeOrderByItemUseResultAsc(Date useTime, String itemCode);
}