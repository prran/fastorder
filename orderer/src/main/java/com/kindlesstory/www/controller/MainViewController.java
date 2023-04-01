package com.kindlesstory.www.controller;

import com.kindlesstory.www.exception.PermissionException;
import javax.servlet.http.HttpServletRequest;
import org.springframework.transaction.annotation.Transactional;
import java.util.Map;
import com.kindlesstory.www.exception.FormatDismatchException;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;
import com.kindlesstory.www.data.dto.SimpleItem;
import com.kindlesstory.www.data.jpa.table.Item;
import com.kindlesstory.www.exception.DatabaseException;
import java.util.List;
import com.kindlesstory.www.data.dto.MainViewComponentDto;
import java.util.ArrayList;
import com.kindlesstory.www.data.rest.RestData;
import com.kindlesstory.www.service.hybrid.LoginService;
import com.kindlesstory.www.service.function.JsonService;
import com.kindlesstory.www.validator.multi.FormatValidator;
import com.kindlesstory.www.service.model.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import com.kindlesstory.www.service.model.KategorieService;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.context.annotation.Profile;

@Profile({ "real" })
@RestController
public class MainViewController
{
    private static final String REST_PATH = "/main/component/information";
    private static final String PAGING_REST_PATH = "/main/paging";
    private static final String USER_ITEM_REST_PATH = "/main/user/item";
    private static final String ITEM_REMOVE_PATH = "/main/item/remove";
    private static final String ITEM_RECOVERY_PATH = "/main/item/recovery";
    private static final String ITEM_AUTH_PATH = "/main/item/permission";
    private static final String ITEM_SEARCH_PATH = "/main/item/search";
    private static final int PRE_PAGING = 15;
    private static final int PAGING = 10;
    @Autowired
    private KategorieService kategorieService;
    @Autowired
    private ItemService itemService;
    @Autowired
    private FormatValidator formatValidator;
    @Autowired
    private JsonService jsonService;
    @Autowired
    private LoginService loginService;
    
    @GetMapping(REST_PATH)
    public RestData returnComponents() {
        final List<MainViewComponentDto> components = new ArrayList<MainViewComponentDto>();
        final RestData restdata = new RestData();
        restdata.setLocation("/main/component/information");
        restdata.setStatus(200);
        restdata.setMessage("OK");
        try {
            final List<String> kategorieList = this.kategorieService.getNames();
            for (final String kategorie : kategorieList) {
                final List<Item> items = this.itemService.findItemsByKategorieName(kategorie, 0, PRE_PAGING);
                final List<SimpleItem> simpleItems = this.itemService.alterToSimpleItems(items);
                final MainViewComponentDto component = new MainViewComponentDto();
                component.setKategorie(kategorie);
                component.setItems(simpleItems);
                components.add(component);
            }
            restdata.setValue(components);
        }
        catch (DatabaseException e) {
            restdata.setStatus(503);
            restdata.setMessage("Service Unvailable");
            return restdata;
        }
        return restdata;
    }
    
    @GetMapping(PAGING_REST_PATH)
    @Transactional
    public RestData paging(@RequestParam("value") String value, @RequestParam("status") final String status, @RequestParam("message") final String message, @RequestParam("location") final String location, @RequestParam(value = "param", required = false) final String param) {
        final RestData restdata = new RestData();
        restdata.setLocation("/main/paging");
        restdata.setParam(value);
        restdata.setStatus(200);
        restdata.setMessage("OK");
        Map<String, String> map = null;
        try {
            map = formatValidator.getValidationedMap(value, 3, new String[] { "!kategorie", "!lastItemCode", "searchLetter" });
            this.formatValidator.itemRefCodeValidation((String)map.get("lastItemCode"));
        }
        catch (FormatDismatchException e) {
            restdata.setStatus(400);
            restdata.setMessage("Bad request");
            return restdata;
        }
        try {
            final List<Item> items = itemService.pagingByItemCode(map.get("kategorie"), map.get("lastItemCode"), map.get("searchLetter"), PAGING);
            final List<SimpleItem> simpleItems = itemService.alterToSimpleItems(items);
            restdata.setValue(simpleItems);
        }
        catch (DatabaseException e2) {
            restdata.setStatus(503);
            restdata.setMessage("Service Unvailable");
        }
        return restdata;
    }
    
    @GetMapping(USER_ITEM_REST_PATH)
    public RestData returnUserItems(@RequestParam("value") String value, @RequestParam("status") final String status, @RequestParam("message") final String message, @RequestParam("location") final String location, @RequestParam(value = "param", required = false) final String param, final HttpServletRequest request) {
        final RestData restdata = new RestData();
        restdata.setLocation("/main/user/item");
        restdata.setParam(value);
        restdata.setStatus(200);
        restdata.setMessage("OK");
        try {
            final Map<String, String> valueMap = jsonService.parse(value);
            final String passId = loginService.authorization(valueMap, request);
            final List<Item> items = itemService.findById(passId);
            final List<SimpleItem> simpleItems = itemService.alterToSimpleItems(items);
            restdata.setValue(simpleItems);
        }
        catch (DatabaseException e) {
            restdata.setStatus(503);
            restdata.setMessage("Service Unvailable");
        }
        catch (PermissionException e2) {
            restdata.setStatus(422);
            restdata.setMessage("Unprocessable Entity");
        }
        return restdata;
    }
    
    @GetMapping(ITEM_AUTH_PATH)
    public RestData checkItemAuth(@RequestParam("value") String value, @RequestParam("status") final String status, @RequestParam("message") final String message, @RequestParam("location") final String location, @RequestParam(value = "param", required = false) final String param, final HttpServletRequest request) {
        final RestData restdata = new RestData();
        restdata.setLocation("/main/item/permission");
        restdata.setParam(value);
        restdata.setStatus(200);
        restdata.setMessage("OK");
        try {
            final Map<String, String> map = formatValidator.getValidationedMap(value, 4, new String[] { "sessionId", "token", "password", "!itemRefCode" });
            this.formatValidator.itemRefCodeValidation(map.get("itemRefCode"));
        }
        catch (FormatDismatchException e2) {
            restdata.setStatus(400);
            restdata.setMessage("Bad request");
            return restdata;
        }
        try {
            final Map<String, String> jsonMap = jsonService.parse(value);
            final String encodePassword = jsonMap.remove("password");
            final String itemCode = jsonMap.remove("itemRefCode");
            boolean isPassed = false;
            final String passId = loginService.authorization(jsonMap, request);
            isPassed = this.itemService.authorization(passId, itemCode);
            if (!isPassed) {
                if (this.itemService.itemPasswordConfirm(itemCode, encodePassword)) {
                    this.itemService.addAuthorization(passId, itemCode);
                    restdata.setValue(true);
                }
                else {
                    restdata.setValue(false);
                }
            }
            else {
                restdata.setValue(true);
            }
        }
        catch (NullPointerException | FormatDismatchException ex2) {
            restdata.setStatus(400);
            restdata.setMessage("Bad request");
        }
        catch (DatabaseException e3) {
            restdata.setStatus(503);
            restdata.setMessage("Service Unvailable");
        }
        catch (PermissionException e4) {
            restdata.setStatus(422);
            restdata.setMessage("Unprocessable Entity");
        }
        return restdata;
    }
    
    @GetMapping(ITEM_REMOVE_PATH)
    public RestData setItemDeathMode(@RequestParam("value") String value, @RequestParam("status") final String status, @RequestParam("message") final String message, @RequestParam("location") final String location, @RequestParam(value = "param", required = false) final String param, final HttpServletRequest request) {
        final RestData restdata = new RestData();
        restdata.setLocation("/main/item/remove");
        restdata.setParam(value);
        restdata.setStatus(200);
        restdata.setMessage("OK");
        try {
            final Map<String, String> map = formatValidator.getValidationedMap(value, 3, new String[] { "sessionId", "token", "!itemRefCode" });
            this.formatValidator.itemRefCodeValidation(map.get("itemRefCode"));
        }
        catch (FormatDismatchException e2) {
            restdata.setStatus(400);
            restdata.setMessage("Bad request");
            return restdata;
        }
        try {
            final Map<String, String> jsonMap = jsonService.parse(value);
            final String itemCode = jsonMap.remove("itemRefCode");
            final String passId = loginService.authorization(jsonMap, request);
            boolean isPassed = false;
            isPassed = itemService.authorization(passId, itemCode);
            if (isPassed) {
                itemService.setDeathTime(itemCode);
                restdata.setValue(true);
            }
            else {
                restdata.setValue(false);
            }
        }
        catch (NullPointerException | FormatDismatchException ex2) {
            restdata.setStatus(400);
            restdata.setMessage("Bad request");
        }
        catch (DatabaseException e3) {
            restdata.setStatus(503);
            restdata.setMessage("Service Unvailable");
        }
        catch (PermissionException e4) {
            restdata.setStatus(422);
            restdata.setMessage("Unprocessable Entity");
        }
        return restdata;
    }
    
    @GetMapping(ITEM_RECOVERY_PATH)
    public RestData recoveryItemDeathMode(@RequestParam("value") String value, @RequestParam("status") final String status, @RequestParam("message") final String message, @RequestParam("location") final String location, @RequestParam(value = "param", required = false) final String param, final HttpServletRequest request) {
        final RestData restdata = new RestData();
        restdata.setLocation("/main/item/recovery");
        restdata.setParam(value);
        restdata.setStatus(200);
        restdata.setMessage("OK");
        try {
            final Map<String, String> map = formatValidator.getValidationedMap(value, 3, new String[] { "sessionId", "token", "!itemRefCode" });
            this.formatValidator.itemRefCodeValidation(map.get("itemRefCode"));
        }
        catch (FormatDismatchException e2) {
            restdata.setStatus(400);
            restdata.setMessage("Bad request");
            return restdata;
        }
        try {
            final Map<String, String> jsonMap = jsonService.parse(value);
            final String itemCode = jsonMap.remove("itemRefCode");
            final String passId = loginService.authorization(jsonMap, request);
            boolean isPassed = false;
            isPassed = itemService.authorization(passId, itemCode);
            if (isPassed) {
                itemService.removeDeathTime(itemCode);
                restdata.setValue(true);
            }
            else {
                restdata.setValue(false);
            }
        }
        catch (NullPointerException | FormatDismatchException ex2) {
            restdata.setStatus(400);
            restdata.setMessage("Bad request");
        }
        catch (DatabaseException e3) {
            restdata.setStatus(503);
            restdata.setMessage("Service Unvailable");
        }
        catch (PermissionException e4) {
            restdata.setStatus(422);
            restdata.setMessage("Unprocessable Entity");
        }
        return restdata;
    }
    
    @GetMapping(ITEM_SEARCH_PATH)
    public RestData itemSearch(@RequestParam("value") String value, @RequestParam("status") final String status, @RequestParam("message") final String message, @RequestParam("location") final String location, @RequestParam(value = "param", required = false) final String param, final HttpServletRequest request) {
        final RestData restdata = new RestData();
        restdata.setLocation("/main/item/search");
        restdata.setStatus(200);
        restdata.setMessage("OK");
        restdata.setParam( value);
        Map<String, String> map;
        try {
            map = (Map<String, String>)this.formatValidator.getValidationedMap(value, 1, new String[] { "letter" });
        }
        catch (FormatDismatchException e) {
            restdata.setStatus(400);
            restdata.setMessage("Bad request");
            return restdata;
        }
        try {
            final String letter = map.get("letter");
            if (letter.equals("") || letter == null) {
                return returnComponents();
            }
            final List<String> kategorieList = kategorieService.getNames();
            final List<MainViewComponentDto> components = new ArrayList<MainViewComponentDto>();
            for (final String kategorie : kategorieList) {
                final List<Item> items = this.itemService.findByItemNamePart(kategorie, map.get("letter"), 15);
                final List<SimpleItem> simpleItems = this.itemService.alterToSimpleItems(items);
                final MainViewComponentDto component = new MainViewComponentDto();
                component.setKategorie(kategorie);
                component.setItems(simpleItems);
                components.add(component);
            }
            restdata.setValue(components);
        }
        catch (DatabaseException e2) {
            restdata.setStatus(503);
            restdata.setMessage("Service Unvailable");
        }
        return restdata;
    }
}