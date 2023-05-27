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

import com.kindlesstory.www.data.rest.Rest;
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
    private final static String REST_PATH = "/main/component/information";
    private final static String PAGING_REST_PATH = "/main/paging";
    private final static String USER_ITEM_REST_PATH = "/main/user/item";
    private final static String ITEM_REMOVE_PATH = "/main/item/remove";
    private final static String ITEM_RECOVERY_PATH = "/main/item/recovery";
    private final static String ITEM_AUTH_PATH = "/main/item/permission";
    private final static String ITEM_SEARCH_PATH = "/main/item/search";
    private final static int PRE_PAGING = 15;
    private final static int PAGING = 10;
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
        List<MainViewComponentDto> components = new ArrayList<MainViewComponentDto>();
        RestData restdata = new RestData();
        restdata.setLocation(REST_PATH);
        restdata.setStatus(200);
        restdata.setMessage(Rest.OK);
        try {
            List<String> kategorieList = this.kategorieService.getNames();
            for (String kategorie : kategorieList) {
                List<Item> items = this.itemService.findItemsByKategorieName(kategorie, 0, PRE_PAGING);
                List<SimpleItem> simpleItems = this.itemService.alterToSimpleItems(items);
                MainViewComponentDto component = new MainViewComponentDto();
                component.setKategorie(kategorie);
                component.setItems(simpleItems);
                components.add(component);
            }
            restdata.setValue(components);
        }
        catch (DatabaseException e) {
            restdata.setStatus(503);
            restdata.setMessage(Rest.SERVICE_UNVAILABLE);
            return restdata;
        }
        return restdata;
    }
    
    @GetMapping(PAGING_REST_PATH)
    @Transactional
    public RestData paging(@RequestParam("value") String value, 
    						@RequestParam("status") String status, 
    						@RequestParam("message") String message, 
    						@RequestParam("location") String location, 
    						@RequestParam(value = "param", required = false) String param) {
        RestData restdata = new RestData();
        restdata.setLocation(PAGING_REST_PATH);
        restdata.setParam(value);
        restdata.setStatus(200);
        restdata.setMessage(Rest.OK);
        Map<String, String> map = null;
        try {
            map = formatValidator.getValidationedMap(value, 3, "!kategorie", "!lastItemCode", "searchLetter");
            this.formatValidator.itemRefCodeValidation((String)map.get("lastItemCode"));
        }
        catch (FormatDismatchException e) {
            restdata.setStatus(400);
            restdata.setMessage(Rest.BAD_REQUEST);
            return restdata;
        }
        try {
            List<Item> items = itemService.pagingByItemCode(map.get("kategorie"), map.get("lastItemCode"), map.get("searchLetter"), PAGING);
            List<SimpleItem> simpleItems = itemService.alterToSimpleItems(items);
            restdata.setValue(simpleItems);
        }
        catch (DatabaseException e2) {
            restdata.setStatus(503);
            restdata.setMessage(Rest.SERVICE_UNVAILABLE);
        }
        return restdata;
    }
    
    @GetMapping(USER_ITEM_REST_PATH)
    public RestData returnUserItems(@RequestParam("value") String value, 
    								@RequestParam("status") String status, 
    								@RequestParam("message") String message, 
    								@RequestParam("location") String location, 
    								@RequestParam(value = "param", required = false) String param, 
    								HttpServletRequest request) {
    	RestData restdata = new RestData();
        restdata.setLocation(USER_ITEM_REST_PATH);
        restdata.setParam(value);
        restdata.setStatus(200);
        restdata.setMessage(Rest.OK);
        try {
            Map<String, String> valueMap = jsonService.parse(value);
            String passId = loginService.authorization(valueMap, request);
            List<Item> items = itemService.findById(passId);
            List<SimpleItem> simpleItems = itemService.alterToSimpleItems(items);
            restdata.setValue(simpleItems);
        }
        catch (DatabaseException e) {
            restdata.setStatus(503);
            restdata.setMessage(Rest.SERVICE_UNVAILABLE);
        }
        catch (PermissionException e2) {
            restdata.setStatus(422);
            restdata.setMessage(Rest.UNPROCESSABLE_ENTITY);
        }
        return restdata;
    }
    
    @GetMapping(ITEM_AUTH_PATH)
    public RestData checkItemAuth(@RequestParam("value") String value, 
    								@RequestParam("status") String status, 
    								@RequestParam("message") String message, 
    								@RequestParam("location") String location, 
    								@RequestParam(value = "param", required = false) String param, 
    								HttpServletRequest request) {
        RestData restdata = new RestData();
        restdata.setLocation(ITEM_AUTH_PATH);
        restdata.setParam(value);
        restdata.setStatus(200);
        restdata.setMessage(Rest.OK);
        try {
            Map<String, String> map = formatValidator.getValidationedMap(value, 4, "sessionId", "token", "password", "!itemRefCode");
            this.formatValidator.itemRefCodeValidation(map.get("itemRefCode"));
        }
        catch (FormatDismatchException e2) {
            restdata.setStatus(400);
            restdata.setMessage(Rest.BAD_REQUEST);
            return restdata;
        }
        try {
            Map<String, String> jsonMap = jsonService.parse(value);
            String encodePassword = jsonMap.remove("password");
            String itemCode = jsonMap.remove("itemRefCode");
            boolean isPassed = false;
            String passId = loginService.authorization(jsonMap, request);
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
            restdata.setMessage(Rest.BAD_REQUEST);
        }
        catch (DatabaseException e3) {
            restdata.setStatus(503);
            restdata.setMessage(Rest.SERVICE_UNVAILABLE);
        }
        catch (PermissionException e4) {
            restdata.setStatus(422);
            restdata.setMessage(Rest.UNPROCESSABLE_ENTITY);
        }
        return restdata;
    }
    
    @GetMapping(ITEM_REMOVE_PATH)
    public RestData setItemDeathMode(@RequestParam("value") String value, 
    									@RequestParam("status") String status, 
    									@RequestParam("message") String message, 
    									@RequestParam("location") String location, 
    									@RequestParam(value = "param", required = false) String param, 
    									HttpServletRequest request) {
        RestData restdata = new RestData();
        restdata.setLocation(ITEM_REMOVE_PATH);
        restdata.setParam(value);
        restdata.setStatus(200);
        restdata.setMessage(Rest.OK);
        try {
            Map<String, String> map = formatValidator.getValidationedMap(value, 3, "sessionId", "token", "!itemRefCode");
            this.formatValidator.itemRefCodeValidation(map.get("itemRefCode"));
        }
        catch (FormatDismatchException e2) {
            restdata.setStatus(400);
            restdata.setMessage(Rest.BAD_REQUEST);
            return restdata;
        }
        try {
            Map<String, String> jsonMap = jsonService.parse(value);
            String itemCode = jsonMap.remove("itemRefCode");
            String passId = loginService.authorization(jsonMap, request);
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
            restdata.setMessage(Rest.BAD_REQUEST);
        }
        catch (DatabaseException e3) {
            restdata.setStatus(503);
            restdata.setMessage(Rest.SERVICE_UNVAILABLE);
        }
        catch (PermissionException e4) {
            restdata.setStatus(422);
            restdata.setMessage(Rest.UNPROCESSABLE_ENTITY);
        }
        return restdata;
    }
    
    @GetMapping(ITEM_RECOVERY_PATH)
    public RestData recoveryItemDeathMode(@RequestParam("value") String value, 
    										@RequestParam("status") String status, 
    										@RequestParam("message") String message, 
    										@RequestParam("location") String location, 
    										@RequestParam(value = "param", required = false) String param, 
    										HttpServletRequest request) {
        RestData restdata = new RestData();
        restdata.setLocation(ITEM_RECOVERY_PATH);
        restdata.setParam(value);
        restdata.setStatus(200);
        restdata.setMessage(Rest.OK);
        try {
            Map<String, String> map = formatValidator.getValidationedMap(value, 3, "sessionId", "token", "!itemRefCode");
            this.formatValidator.itemRefCodeValidation(map.get("itemRefCode"));
        }
        catch (FormatDismatchException e2) {
            restdata.setStatus(400);
            restdata.setMessage(Rest.BAD_REQUEST);
            return restdata;
        }
        try {
            Map<String, String> jsonMap = jsonService.parse(value);
            String itemCode = jsonMap.remove("itemRefCode");
            String passId = loginService.authorization(jsonMap, request);
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
            restdata.setMessage(Rest.BAD_REQUEST);
        }
        catch (DatabaseException e3) {
            restdata.setStatus(503);
            restdata.setMessage(Rest.SERVICE_UNVAILABLE);
        }
        catch (PermissionException e4) {
            restdata.setStatus(422);
            restdata.setMessage(Rest.UNPROCESSABLE_ENTITY);
        }
        return restdata;
    }
    
    @GetMapping(ITEM_SEARCH_PATH)
    public RestData itemSearch(@RequestParam("value") String value, 
    							@RequestParam("status") String status, 
    							@RequestParam("message") String message, 
    							@RequestParam("location") String location, 
    							@RequestParam(value = "param", required = false) String param, 
    							HttpServletRequest request) {
        RestData restdata = new RestData();
        restdata.setLocation(ITEM_SEARCH_PATH);
        restdata.setStatus(200);
        restdata.setMessage(Rest.OK);
        restdata.setParam( value);
        Map<String, String> map;
        try {
            map = (Map<String, String>)this.formatValidator.getValidationedMap(value, 1, "letter");
        }
        catch (FormatDismatchException e) {
            restdata.setStatus(400);
            restdata.setMessage(Rest.BAD_REQUEST);
            return restdata;
        }
        try {
            String letter = map.get("letter");
            if (letter.equals("") || letter == null) {
                return returnComponents();
            }
            List<String> kategorieList = kategorieService.getNames();
            List<MainViewComponentDto> components = new ArrayList<MainViewComponentDto>();
            for (String kategorie : kategorieList) {
                List<Item> items = this.itemService.findByItemNamePart(kategorie, map.get("letter"), PRE_PAGING);
                List<SimpleItem> simpleItems = this.itemService.alterToSimpleItems(items);
                MainViewComponentDto component = new MainViewComponentDto();
                component.setKategorie(kategorie);
                component.setItems(simpleItems);
                components.add(component);
            }
            restdata.setValue(components);
        }
        catch (DatabaseException e2) {
            restdata.setStatus(503);
            restdata.setMessage(Rest.SERVICE_UNVAILABLE);
        }
        return restdata;
    }
}