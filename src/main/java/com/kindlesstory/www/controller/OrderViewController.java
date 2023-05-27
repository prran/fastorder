package com.kindlesstory.www.controller;

import com.kindlesstory.www.data.dto.OrderExecuteMetadata;
import org.springframework.web.bind.annotation.GetMapping;
import com.kindlesstory.www.exception.PermissionException;
import java.util.Map;
import com.kindlesstory.www.exception.DatabaseException;
import com.kindlesstory.www.data.jpa.table.id.ItemId;
import com.kindlesstory.www.exception.UnprocessableCodeException;
import com.kindlesstory.www.exception.DecryptException;
import com.kindlesstory.www.exception.FormatDismatchException;
import com.kindlesstory.www.data.rest.Rest;
import com.kindlesstory.www.data.rest.RestData;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.InitBinder;
import com.kindlesstory.www.validator.RestDataXssValidator;
import org.springframework.web.bind.WebDataBinder;
import com.kindlesstory.www.service.model.ItemService;
import com.kindlesstory.www.service.hybrid.LoginService;
import com.kindlesstory.www.validator.JavaScriptCodeDeepValidator;
import org.springframework.beans.factory.annotation.Autowired;
import com.kindlesstory.www.validator.multi.FormatValidator;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.context.annotation.Profile;

@Profile({ "real" })
@RestController
public class OrderViewController
{
    private final static String CREATE_PATH = "/order/create";
    private final static String EXECUTE_PATH = "/order/execute";
    private final static String EDIT_PATH = "/order/edit";
    private final static String RECODE_PATH = "/order/execute/recode";
    private final static String EDIT_AUTH = "/order/edit/auth";
    private final static String KATEGORIE = "정렬";
    @Autowired
    private FormatValidator formatValidator;
    @Autowired
    private JavaScriptCodeDeepValidator deepValidator;
    @Autowired
    private LoginService loginService;
    @Autowired
    private ItemService itemService;
    
    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.setValidator(new RestDataXssValidator("sesstionId", "token", "orderContext"));
    }
    
    @GetMapping(CREATE_PATH)
    public RestData createOrder(@RequestParam("value") String value,
    							@RequestParam("status") String status, 
    							@RequestParam("message") String message, 
    							@RequestParam("location") String location, 
    							@RequestParam(value = "param", required = false) String param, 
    							HttpServletRequest request) {
        RestData restdata = new RestData();
        Map<String, String> dataMap = null;
        String context = null;
        restdata.setLocation(CREATE_PATH);
        restdata.setParam((Object)value);
        restdata.setStatus(201);
        restdata.setMessage(Rest.OK);
        try {
            dataMap = formatValidator.getValidationedMap(value, 8, "sessionId", "token", "password", "!title", "!context", "!iv", "!salt", "!passPhrase");
            context = deepValidator.getValidatedContext(dataMap.get("context"), dataMap.remove("iv"), dataMap.remove("salt"), dataMap.remove("passPhrase"));
        }
        catch (FormatDismatchException | ClassCastException | DecryptException ex2) {
            restdata.setStatus(400);
            restdata.setMessage(Rest.BAD_REQUEST);
            return restdata;
        }
        catch (UnprocessableCodeException e2) {
            restdata.setStatus(451);
            restdata.setMessage(Rest.UNAVAILABLE_FOR_LEGAL_REASONS);
            return restdata;
        }
        ItemId itemId = new ItemId();
        itemId.setItemName(dataMap.get("title"));
        itemId.setKateName(KATEGORIE);
        try {
            if (this.itemService.existByPrimaryKey(itemId)) {
                restdata.setMessage("multiple index value");
                restdata.setValue(false);
                return restdata;
            }
        }
        catch (DatabaseException e3) {
            restdata.setStatus(503);
            restdata.setMessage(Rest.SERVICE_UNVAILABLE);
            return restdata;
        }
        String itemName = dataMap.remove("title");
        dataMap.remove("context");
        String password = dataMap.remove("password");
        try {
            String passId = loginService.authorization(dataMap, request);
            String refCode = itemService.insertSortItem(passId, itemName, context, password);
            restdata.setValue(refCode);
        }
        catch (PermissionException e4) {
            restdata.setStatus(422);
            restdata.setMessage(Rest.UNPROCESSABLE_ENTITY);
        }
        catch (DatabaseException e5) {
            restdata.setStatus(503);
            restdata.setMessage(Rest.SERVICE_UNVAILABLE);
            return restdata;
        }
        return restdata;
    }
    
    @GetMapping(EDIT_PATH)
    public RestData editOrder(@RequestParam("value") String value, 
    							@RequestParam("status") String status, 
    							@RequestParam("message") String message, 
    							@RequestParam("location") String location, 
    							@RequestParam(value = "param", required = false) String param, 
    							HttpServletRequest request) {
        RestData restdata = new RestData();
        Map<String, String> dataMap = null;
        String context = null;
        restdata.setLocation(EDIT_PATH);
        restdata.setParam(value);
        restdata.setStatus(201);
        restdata.setMessage(Rest.OK);
        try {
            dataMap = formatValidator.getValidationedMap(value, 8, "sessionId", "token", "!title", "!context", "!iv", "!salt", "!passPhrase", "!itemRefCode");
            context = deepValidator.getValidatedContext(dataMap.get("context"), dataMap.remove("iv"), dataMap.remove("salt"), dataMap.remove("passPhrase"));
            formatValidator.itemRefCodeValidation(dataMap.get("itemRefCode"));
        }
        catch (FormatDismatchException | ClassCastException | DecryptException ex2) {
            restdata.setStatus(400);
            restdata.setMessage(Rest.BAD_REQUEST);
            return restdata;
        }
        catch (UnprocessableCodeException e2) {
            restdata.setStatus(451);
            restdata.setMessage(Rest.UNAVAILABLE_FOR_LEGAL_REASONS);
            return restdata;
        }
        ItemId itemId = new ItemId();
        itemId.setItemName(dataMap.get("title"));
        itemId.setKateName(KATEGORIE);
        try {	
            if (!this.itemService.qualsItemNameByItemRefCode(dataMap.get("title"), dataMap.get("itemRefCode")) && this.itemService.existByPrimaryKey(itemId)) {
                restdata.setMessage("multiple index value");
                restdata.setValue(false);
                return restdata;
            }
        }
        catch (DatabaseException e3) {
            restdata.setStatus(503);
            restdata.setMessage(Rest.SERVICE_UNVAILABLE);
            return restdata;
        }
        String itemName = dataMap.remove("title");
        dataMap.remove("context");
        String itemRefCode = dataMap.remove("itemRefCode");
        try {
            String passId = this.loginService.authorization(dataMap, request);
            this.itemService.updateSortItem(itemName, context, passId, itemRefCode);
            restdata.setValue(true);
        }
        catch (PermissionException e4) {
            restdata.setStatus(422);
            restdata.setMessage(Rest.UNPROCESSABLE_ENTITY);
        }
        catch (DatabaseException e5) {
            restdata.setStatus(503);
            restdata.setMessage(Rest.SERVICE_UNVAILABLE);
            return restdata;
        }
        return restdata;
    }
    
    @GetMapping(EDIT_AUTH)
    public RestData checkEditAuth(@RequestParam("value") String value, 
    								@RequestParam("status") String status, 
    								@RequestParam("message") String message, 
    								@RequestParam("location") String location, 
    								@RequestParam(value = "param", required = false) String param, 
    								HttpServletRequest request) {
        RestData restdata = new RestData();
        Map<String, String> dataMap = null;
        restdata.setLocation(EDIT_AUTH);
        restdata.setParam(value);
        restdata.setStatus(200);
        restdata.setMessage(Rest.OK);
        try {
            dataMap = formatValidator.getValidationedMap(value, 3, "!sessionId", "token", "!itemRefCode");
            formatValidator.itemRefCodeValidation(dataMap.get("itemRefCode"));
        }
        catch (FormatDismatchException e) {
            restdata.setStatus(400);
            restdata.setMessage(Rest.BAD_REQUEST);
            return restdata;
        }
        String itemRefCode = dataMap.remove("itemRefCode");
        try {
            String passId = loginService.authorization(dataMap, request);
            if (itemService.authorization(passId, itemRefCode)) {
                restdata.setValue(itemService.getOrderContext(itemRefCode));
            }
        }
        catch (DatabaseException e2) {
            restdata.setStatus(503);
            restdata.setMessage(Rest.SERVICE_UNVAILABLE);
        }
        catch (PermissionException e3) {
            restdata.setStatus(422);
            restdata.setMessage(Rest.UNPROCESSABLE_ENTITY);
        }
        return restdata;
    }
    
    @GetMapping(EXECUTE_PATH)
    public RestData executeOrder(@RequestParam("value") String value, 
    								@RequestParam("status") String status, 
    								@RequestParam("message") String message, 
    								@RequestParam("location") String location, 
    								@RequestParam(value = "param", required = false) String param, 
    								HttpServletRequest request) {
        RestData restdata = new RestData();
        Map<String, String> dataMap = null;
        restdata.setLocation(EXECUTE_PATH);
        restdata.setParam(value);
        restdata.setStatus(200);
        restdata.setMessage(Rest.OK);
        try {
            dataMap = formatValidator.getValidationedMap(value, 1, "!itemRefCode");
            formatValidator.itemRefCodeValidation(dataMap.get("itemRefCode"));
        }
        catch (FormatDismatchException e) {
            restdata.setStatus(400);
            restdata.setMessage(Rest.BAD_REQUEST);
            return restdata;
        }
        try {
            OrderExecuteMetadata executeMetadata = itemService.getOrderExecuteMetadata((String)dataMap.get("itemRefCode"));
            restdata.setValue((Object)executeMetadata);
        }
        catch (DatabaseException e2) {
            restdata.setStatus(503);
            restdata.setMessage(Rest.SERVICE_UNVAILABLE);
        }
        return restdata;
    }
    
    @GetMapping(RECODE_PATH)
    public RestData recodeExecuteResult(@RequestParam("value") String value, 
    									@RequestParam("status") String status, 
    									@RequestParam("message") String message, 
    									@RequestParam("location") String location, 
    									@RequestParam(value = "param", required = false) String param, 
    									HttpServletRequest request) {
        RestData restdata = new RestData();
        Map<String, String> dataMap = null;
        restdata.setLocation(RECODE_PATH);
        restdata.setParam(value);
        restdata.setStatus(200);
        restdata.setMessage(Rest.OK);
        try {
            dataMap = formatValidator.getValidationedMap(value, 2, "!itemRefCode", "!recode");
            this.formatValidator.itemRefCodeValidation(dataMap.get("itemRefCode"));
            this.formatValidator.recodeValidation(dataMap.get("recode"));
        }
        catch (FormatDismatchException e) {
            restdata.setStatus(400);
            restdata.setMessage(Rest.BAD_REQUEST);
            return restdata;
        }
        try {
            this.itemService.addItemUseRecode(dataMap.get("itemRefCode"), dataMap.get("recode"));
            OrderExecuteMetadata metadata = itemService.getOrderExecuteMetadata(dataMap.get("itemRefCode"));
            restdata.setValue(metadata);
        }
        catch (DatabaseException e2) {
            restdata.setStatus(503);
            restdata.setMessage(Rest.SERVICE_UNVAILABLE);
        }
        return restdata;
    }
}