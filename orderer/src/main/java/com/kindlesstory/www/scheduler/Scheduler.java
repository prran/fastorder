package com.kindlesstory.www.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.beans.factory.annotation.Autowired;
import com.kindlesstory.www.service.model.ItemService;
import org.springframework.stereotype.Component;

@Component
public class Scheduler
{
    @Autowired
    private ItemService itemService;
    
    @Scheduled(cron = "0 0 4 1 * *")
    public void deleteItems() {
        itemService.updateItemList();
    }
}