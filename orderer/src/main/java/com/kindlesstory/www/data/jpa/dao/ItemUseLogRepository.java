package com.kindlesstory.www.data.jpa.dao;

import java.util.Date;
import java.util.List;
import com.kindlesstory.www.data.jpa.table.id.ItemUseLogId;
import com.kindlesstory.www.data.jpa.table.ItemUseLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemUseLogRepository extends JpaRepository<ItemUseLog, ItemUseLogId>
{
    List<ItemUseLog> findByItemRefCode(final String itemCode);
    
    ItemUseLog findTop1ByItemUseResultAndItemRefCodeOrderByItemUseTimeDesc(final Long resultTime, final String itemCode);
    
    List<ItemUseLog> findTop5ByItemUseTimeGreaterThanAndItemRefCodeOrderByItemUseResultAsc(final Date useTime, final String itemCode);
    
    List<ItemUseLog> findTop5ByItemUseTimeLessThanAndItemRefCodeOrderByItemUseResultAsc(final Date useTime, final String itemCode);
}