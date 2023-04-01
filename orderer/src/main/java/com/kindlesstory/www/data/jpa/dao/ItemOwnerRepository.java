package com.kindlesstory.www.data.jpa.dao;

import java.util.List;
import com.kindlesstory.www.data.jpa.table.id.ItemOwnerId;
import com.kindlesstory.www.data.jpa.table.ItemOwner;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemOwnerRepository extends JpaRepository<ItemOwner, ItemOwnerId>
{
    List<ItemOwner> findByItemOwnerCode(final String ownerCode);
}