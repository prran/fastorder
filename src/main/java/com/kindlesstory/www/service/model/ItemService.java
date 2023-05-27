package com.kindlesstory.www.service.model;

import com.kindlesstory.www.exception.CrushDataStoredException;
import org.apache.commons.codec.binary.Base64;
import com.kindlesstory.www.data.jpa.table.User;
import com.kindlesstory.www.data.dto.OrderExecuteMetadata;
import com.kindlesstory.www.exception.NoSearchDatabaseException;
import com.kindlesstory.www.data.jpa.table.ItemOwnerCode;
import com.kindlesstory.www.data.jpa.table.ItemRefCode;
import com.kindlesstory.www.data.jpa.table.id.ItemId;
import com.kindlesstory.www.data.jpa.table.ItemUseLog;
import com.kindlesstory.www.data.jpa.table.SortItem;
import java.util.Date;
import com.kindlesstory.www.data.jpa.table.id.ItemOwnerId;
import com.kindlesstory.www.data.jpa.table.ItemOwner;
import com.kindlesstory.www.exception.FormatDismatchException;
import java.util.ArrayList;
import com.kindlesstory.www.data.dto.SimpleItem;
import com.kindlesstory.www.exception.DatabaseException;
import org.springframework.data.domain.PageRequest;
import com.kindlesstory.www.data.jpa.table.Item;
import java.util.List;
import com.kindlesstory.www.data.jpa.dao.custom.CustomQuery;
import com.kindlesstory.www.module.Crypt;
import com.kindlesstory.www.data.jpa.dao.UserRepository;
import com.kindlesstory.www.data.jpa.dao.ItemUseLogRepository;
import com.kindlesstory.www.data.jpa.dao.SortItemRepository;
import com.kindlesstory.www.data.jpa.dao.ItemOwnerRepository;
import com.kindlesstory.www.data.jpa.dao.ItemOwnerCodeRepository;
import com.kindlesstory.www.data.jpa.dao.ItemRefCodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import com.kindlesstory.www.data.jpa.dao.ItemRepository;
import org.springframework.stereotype.Service;
import com.kindlesstory.www.service.inter.DatabaseService;

@Service
public class ItemService implements DatabaseService
{
    private final static int ITEM_DAETH_TERM = 604800000;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private ItemRefCodeRepository itemRefCodeRepository;
    @Autowired
    private ItemOwnerCodeRepository itemOwnerCodeRepository;
    @Autowired
    private ItemOwnerRepository itemOwnerRepository;
    @Autowired
    private SortItemRepository sortItemRepository;
    @Autowired
    private ItemUseLogRepository itemUseLogRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private Crypt crypt;
    @Autowired
    private CustomQuery customQuery;
    
    public List<Item> findItemsByKategorieName(String kategorieName, int page, int size) throws DatabaseException {
        try {
            PageRequest pageRequest = PageRequest.of(page, size);
            return itemRepository.findByKateNameOrderByItemAgeDesc(kategorieName, pageRequest);
        }
        catch (Exception e) {
            throw new DatabaseException();
        }
    }
    
    public List<Item> findById(String id) throws DatabaseException {
        try {
            return itemRepository.findById(id);
        }
        catch (Exception e) {
            throw new DatabaseException();
        }
    }
    
    public int getIndexInKategorie(String kategorieName, String itemCode) throws DatabaseException {
        try {
            Item item = itemRepository.findByItemRefCode(itemCode);
            long count = itemRepository.countByItemAgeGreaterThanAndKateNameIs(item.getItemAge(), kategorieName);
            return (int)count;
        }
        catch (Exception e) {
            throw new DatabaseException();
        }
    }
    
    public List<SimpleItem> alterToSimpleItems(List<Item> items) {
        List<SimpleItem> simpleItems = new ArrayList<SimpleItem>();
        for (Item item : items) {
            SimpleItem simpleItem = new SimpleItem();
            simpleItem.setName(item.getItemName());
            simpleItem.setCode(item.getItemRefCode());
            simpleItem.setKateName(item.getKateName());
            simpleItem.setItemDeath(item.getItemDeath() != null);
            simpleItems.add(simpleItem);
        }
        return simpleItems;
    }
    
    public boolean itemPasswordConfirm(String itemCode, String encodePassword) throws DatabaseException, FormatDismatchException {
        try {
            String password = null;
            if (encodePassword != null) {
                password = crypt.decryptRsa(encodePassword);
                password = crypt.encryptSha256(password);
            }
            Item item = itemRepository.findByItemRefCode(itemCode);
            return item.getItemPassword().equals(password);
        }
        catch (NullPointerException e) {
            throw new FormatDismatchException();
        }
        catch (Exception e2) {
            throw new DatabaseException();
        }
    }
    
    public boolean authorization(String passId, String itemCode) throws DatabaseException {
        try {
            Item item = itemRepository.findByItemRefCode(itemCode);
            String ownerCode = item.getItemOwnerCode();
            List<ItemOwner> itemOwners = itemOwnerRepository.findByItemOwnerCode(ownerCode);
            boolean result = false;
            for (ItemOwner owner : itemOwners) {
                if (owner.getUserId().equals(passId)) {
                    result = true;
                }
            }
            return result;
        }
        catch (Exception e) {
            throw new DatabaseException();
        }
    }
    
    public void addAuthorization(String passId, String itemCode) throws DatabaseException {
        try {
            Item item = itemRepository.findByItemRefCode(itemCode);
            String ownerCode = item.getItemOwnerCode();
            ItemOwnerId id = new ItemOwnerId();
            id.setItemOwnerCode(ownerCode);
            id.setUserId(passId);
            if (!itemOwnerRepository.existsById(id)) {
                ItemOwner itemOwner = new ItemOwner();
                itemOwner.setUserId(passId);
                itemOwner.setItemOwnerCode(ownerCode);
                itemOwner.setItemOwnerAge(new Date(System.currentTimeMillis()));
                itemOwnerRepository.save(itemOwner);
            }
        }
        catch (Exception e) {
            throw new DatabaseException();
        }
    }
    
    public void setDeathTime(String itemCode) throws DatabaseException {
        try {
            long timeLimit = System.currentTimeMillis() - ITEM_DAETH_TERM;
            Date deathTime = new Date(timeLimit);
            Item item = itemRepository.findByItemRefCode(itemCode);
            item.setItemDeath(deathTime);
            itemRepository.save(item);
        }
        catch (Exception e) {
            throw new DatabaseException();
        }
    }
    
    public void removeDeathTime(String itemCode) throws DatabaseException {
        try {
            Item item = itemRepository.findByItemRefCode(itemCode);
            item.setItemDeath(null);
            itemRepository.save(item);
        }
        catch (Exception e) {
            throw new DatabaseException();
        }
    }
    
    public void updateItemList() {
        List<Item> deathItem = itemRepository.findByItemDeathLessThan(new Date(System.currentTimeMillis()));
        for (Item item : deathItem) {
            String itemOwnerCode = item.getItemOwnerCode();
            String itemRefCode = item.getItemRefCode();
            String itemKategorie = item.getKateName();
            itemRepository.delete(item);
            if (itemKategorie.equals("정렬")) {
                SortItem sortItem = (SortItem)sortItemRepository.getOne(itemOwnerCode);
                if (sortItem != null) {
                    sortItemRepository.delete(sortItem);
                }
            }
            List<ItemUseLog> itemUseLog = itemUseLogRepository.findByItemRefCode(itemRefCode);
            if (itemUseLog != null) {
                itemUseLog.stream().forEach(log -> itemUseLogRepository.delete(log));
            }
            List<ItemOwner> itemOwner = itemOwnerRepository.findByItemOwnerCode(itemOwnerCode);
            if (itemOwner != null) {
                itemOwner.stream().forEach(user -> itemOwnerRepository.delete(user));
            }
            try {
                itemRefCodeRepository.deleteById(itemRefCode);
            }
            catch (IllegalArgumentException ex) {}
        }
    }
    
    public boolean existByPrimaryKey(ItemId itemId) throws DatabaseException {
        try {
            return itemRepository.existsById(itemId);
        }
        catch (Exception e) {
            throw new DatabaseException();
        }
    }
    
    public ItemId getPrimaryKeyByItemRefCode(String itemRefCode) {
        try {
            Item item = itemRepository.findByItemRefCode(itemRefCode);
            ItemId itemId = new ItemId();
            itemId.setItemName(item.getItemName());
            itemId.setKateName(item.getKateName());
            return itemId;
        }
        catch (NullPointerException e) {
            return null;
        }
    }
    
    public String insertSortItem(String passId, String itemName, String orderContext, String password) throws DatabaseException {
        String encryptContext = crypt.encryptSimpleAes(orderContext);
        ItemRefCode itemReferenceCode = new ItemRefCode();
        String itemRefCode = "sort" + itemName + System.currentTimeMillis();
        itemRefCode = crypt.encryptMD5(itemRefCode);
        itemReferenceCode.setItemRefCode(itemRefCode);
        ItemOwnerCode itemOwnerCodeTb = new ItemOwnerCode();
        String itemOwnerCode = "group" + itemName + System.currentTimeMillis();
        itemOwnerCode = crypt.encryptMD5(itemOwnerCode);
        itemOwnerCodeTb.setItemOwnerCode(itemOwnerCode);
        SortItem sortItem = new SortItem();
        sortItem.setItemRefCode(itemRefCode);
        sortItem.setSortItemContext(encryptContext);
        sortItem.setSortItemLength(orderContext.length());
        ItemUseLog itemUseLog = new ItemUseLog();
        Date currentTime = new Date(System.currentTimeMillis());
        itemUseLog.setItemRefCode(itemRefCode);
        itemUseLog.setItemUseTime(currentTime);
        ItemOwner itemOwner = new ItemOwner();
        itemOwner.setItemOwnerCode(itemOwnerCode);
        itemOwner.setUserId(passId);
        itemOwner.setItemOwnerAge(currentTime);
        Item item = new Item();
        item.setKateName("정렬");
        item.setItemName(itemName);
        item.setItemRefCode(itemRefCode);
        item.setItemOwnerCode(itemOwnerCode);
        item.setItemAge(currentTime);
        item.setItemRecent(currentTime);
        item.setItemPassword(crypt.encryptSha256(crypt.decryptRsa(password)));
        try {
            itemRefCodeRepository.save(itemReferenceCode);
            itemOwnerCodeRepository.save(itemOwnerCodeTb);
            sortItemRepository.save(sortItem);
            itemOwnerRepository.save(itemOwner);
            itemUseLogRepository.save(itemUseLog);
            itemRepository.save(item);
            return itemRefCode;
        }
        catch (Exception e) {
            throw new DatabaseException();
        }
    }
    
    public void updateSortItem(String itemName, String orderContext, String passId, String itemRefCode) throws DatabaseException {
        SortItem sortItem = sortItemRepository.findById(itemRefCode).orElseThrow(() -> new NoSearchDatabaseException());
        String encryptContext = crypt.encryptSimpleAes(orderContext);
        sortItem.setSortItemContext(encryptContext);
        sortItem.setSortItemLength(orderContext.length());
        ItemUseLog itemUseLog = new ItemUseLog();
        Date currentTime = new Date(System.currentTimeMillis());
        itemUseLog.setItemRefCode(itemRefCode);
        itemUseLog.setItemUseTime(currentTime);
        Item item = itemRepository.findByItemRefCode(itemRefCode);
        item.setItemName(itemName);
        item.setItemRecent(currentTime);
        try {
            sortItemRepository.save(sortItem);
            itemUseLogRepository.save(itemUseLog);
            itemRepository.save(item);
            addAuthorization(passId, itemRefCode);
        }
        catch (Exception e) {
            throw new DatabaseException();
        }
    }
    
    public OrderExecuteMetadata getOrderExecuteMetadata(String itemRefCode) throws DatabaseException {
        OrderExecuteMetadata orderExecuteMetadata = new OrderExecuteMetadata();
        try {
            Item item = itemRepository.findByItemRefCode(itemRefCode);
            SortItem sortItem = sortItemRepository.findById(itemRefCode).orElseThrow(() -> new NoSearchDatabaseException());
            int rank = customQuery.findRankBySortItemLengthAsItemRefCodeInSortItem(itemRefCode);
            List<ItemOwner> ownerIdList = itemOwnerRepository.findByItemOwnerCode(item.getItemOwnerCode());
            List<String> userNameList = new ArrayList<String>();
            ownerIdList.stream().forEach(info -> {
                try {
                    User user = userRepository.findById(info.getUserId()).get();
                    userNameList.add(user.getUserName());
                }
                catch (NullPointerException ex) {}
                return;
            });
            String orderContext = sortItem.getSortItemContext();
            String decodeContext = crypt.decryptSimpleAes(orderContext);
            String newEncodeContext = Base64.encodeBase64String(decodeContext.getBytes());
            orderExecuteMetadata.setItemName(item.getItemName());
            orderExecuteMetadata.setOrderContext(newEncodeContext);
            orderExecuteMetadata.setItemUseResultListTop5(getItemUseResultList(itemRefCode));
            orderExecuteMetadata.setUserNameList(userNameList);
            orderExecuteMetadata.setRank(rank);
        }
        catch (Exception e) {
            throw new DatabaseException();
        }
        return orderExecuteMetadata;
    }
    
    private List<Long> getItemUseResultList(String itemRefCode) throws DatabaseException {
        List<Long> results = new ArrayList<Long>();
        try {
            ItemUseLog editLog = itemUseLogRepository.findTop1ByItemUseResultAndItemRefCodeOrderByItemUseTimeDesc(0L, itemRefCode);
            Date editTime = editLog.getItemUseTime();
            List<ItemUseLog> topRecodes = itemUseLogRepository.findTop5ByItemUseTimeGreaterThanAndItemRefCodeOrderByItemUseResultAsc(editTime, itemRefCode);
            topRecodes.stream().forEach(recode -> results.add(recode.getItemUseResult()));
            if (results.size() < 5) {
                List<ItemUseLog> beforeRecodes = itemUseLogRepository.findTop5ByItemUseTimeLessThanAndItemRefCodeOrderByItemUseResultAsc(editTime, itemRefCode);
                for (ItemUseLog beforeLog : beforeRecodes) {
                    results.add(beforeLog.getItemUseResult());
                    if (results.size() >= 5) {
                        break;
                    }
                }
            }
        }
        catch (Exception e) {
            throw new DatabaseException();
        }
        return results;
    }
    
    public void addItemUseRecode(String itemRefCode, String recode) throws DatabaseException {
        try {
            if (!itemRepository.existsByItemRefCode(itemRefCode)) {
                throw new NoSearchDatabaseException();
            }
            ItemUseLog itemUseLog = new ItemUseLog();
            itemUseLog.setItemRefCode(itemRefCode);
            itemUseLog.setItemUseTime(new Date(System.currentTimeMillis()));
            itemUseLog.setItemUseResult(Long.parseLong(recode));
            itemUseLogRepository.save(itemUseLog);
        }
        catch (Exception e) {
            throw new DatabaseException();
        }
    }
    
    public List<Item> pagingByItemCode(String kategorie, String lastItemCode, String searchLetter, int size) throws DatabaseException {
        if (searchLetter == "null" || searchLetter == null || searchLetter == "") {
            searchLetter = null;
        }
        PageRequest pageRequest = PageRequest.of(0, size);
        Item item = itemRepository.findByItemRefCode(lastItemCode);
        try {
            if (searchLetter == null) {
                return itemRepository.findByItemAgeLessThanAndKateNameIsOrderByItemAgeDesc(item.getItemAge(), kategorie, pageRequest);
            }
            return itemRepository.findByItemAgeLessThanAndKateNameIsAndItemNameContainingOrderByItemAgeDesc(item.getItemAge(), kategorie, searchLetter, pageRequest);
        }
        catch (Exception e) {
            throw new DatabaseException();
        }
    }
    
    public List<Item> findByItemNamePart(String kategorie, String letter, int pagging) throws DatabaseException {
        PageRequest pageRequest = PageRequest.of(0, pagging);
        try {
            return itemRepository.findByItemNameContainingAndKateNameIsOrderByItemAgeDesc(letter, kategorie, pageRequest);
        }
        catch (Exception e) {
            throw new DatabaseException();
        }
    }
    
    public String getOrderContext(String itemRefCode) throws DatabaseException {
        SortItem sortItem = sortItemRepository.findById(itemRefCode).orElseThrow(() -> new NoSearchDatabaseException());
        String orderContext = sortItem.getSortItemContext();
        String encodeContext;
        try {
            String decodeContext = crypt.decryptSimpleAes(orderContext);
            encodeContext = Base64.encodeBase64String(decodeContext.getBytes("utf-8"));
        }
        catch (Exception e) {
            throw new CrushDataStoredException();
        }
        return encodeContext;
    }
    
    public boolean qualsItemNameByItemRefCode(String itemName, String itemRefCode) throws DatabaseException {
        try {
            return itemRepository.findByItemRefCode(itemRefCode).getItemName().equals(itemName);
        }
        catch (Exception e) {
            throw new CrushDataStoredException();
        }
    }
    
    public void deleteItem(String itemCode) {
        Item item = itemRepository.findByItemRefCode(itemCode);
        List<ItemUseLog> itemUseLogs = itemUseLogRepository.findByItemRefCode(itemCode);
        String itemOwnerCode = item.getItemOwnerCode();
        List<ItemOwner> itemOwners = itemOwnerRepository.findByItemOwnerCode(itemOwnerCode);
        String kategorie = item.getKateName();
        ItemOwnerCode ownerCode = itemOwnerCodeRepository.findById(itemOwnerCode).orElse(null);
        ItemRefCode refCode = itemRefCodeRepository.findById(itemCode).orElse(null);
        itemRepository.delete(item);
        for (ItemUseLog itemUseLog : itemUseLogs) {
            itemUseLogRepository.delete(itemUseLog);
        }
        for (ItemOwner itemOwner : itemOwners) {
            itemOwnerRepository.delete(itemOwner);
        }
        if (kategorie.equals("정렬")) {
            SortItem sortItem = sortItemRepository.findById(itemCode).orElse(null);
            sortItemRepository.delete(sortItem);
        }
        itemOwnerCodeRepository.delete(ownerCode);
        itemRefCodeRepository.delete(refCode);
    }
}