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
    private static final String CREATE_PATH = "/order/create";
    private static final String EXECUTE_PATH = "/order/execute";
    private static final String EDIT_PATH = "/order/edit";
    private static final String RECODE_PATH = "/order/execute/recode";
    private static final String EDIT_AUTH = "/order/edit/auth";
    private static final String KATEGORIE = "정렬";
    @Autowired
    private FormatValidator formatValidator;
    @Autowired
    private JavaScriptCodeDeepValidator deepValidator;
    @Autowired
    private LoginService loginService;
    @Autowired
    private ItemService itemService;
    
    @InitBinder
    protected void initBinder(final WebDataBinder binder) {
        binder.setValidator(new RestDataXssValidator(new String[] { "sesstionId", "token", "orderContext" }));
    }
    
    @GetMapping(CREATE_PATH)
    public RestData createOrder(@RequestParam("value") String value, @RequestParam("status") final String status, @RequestParam("message") final String message, @RequestParam("location") final String location, @RequestParam(value = "param", required = false) final String param, final HttpServletRequest request) {
        final RestData restdata = new RestData();
        Map<String, String> dataMap = null;
        String context = null;
        restdata.setLocation("/order/create");
        restdata.setParam((Object)value);
        restdata.setStatus(201);
        restdata.setMessage("OK");
        try {
            dataMap = formatValidator.getValidationedMap(value, 8, new String[] { "sessionId", "token", "password", "!title", "!context", "!iv", "!salt", "!passPhrase" });
            context = deepValidator.getValidatedContext(dataMap.get("context"), dataMap.remove("iv"), dataMap.remove("salt"), dataMap.remove("passPhrase"));
        }
        catch (FormatDismatchException | ClassCastException | DecryptException ex2) {
            restdata.setStatus(400);
            restdata.setMessage("Bad request");
            return restdata;
        }
        catch (UnprocessableCodeException e2) {
            restdata.setStatus(451);
            restdata.setMessage("Unavailable For Legal Reasons");
            return restdata;
        }
        final ItemId itemId = new ItemId();
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
            restdata.setMessage("Service Unvailable");
            return restdata;
        }
        final String itemName = dataMap.remove("title");
        dataMap.remove("context");
        final String password = dataMap.remove("password");
        try {
            final String passId = loginService.authorization(dataMap, request);
            final String refCode = itemService.insertSortItem(passId, itemName, context, password);
            restdata.setValue(refCode);
        }
        catch (PermissionException e4) {
            restdata.setStatus(422);
            restdata.setMessage("Unprocessable Entity");
        }
        catch (DatabaseException e5) {
            restdata.setStatus(503);
            restdata.setMessage("Service Unvailable");
            return restdata;
        }
        return restdata;
    }
    
    @GetMapping(EDIT_PATH)
    public RestData editOrder(@RequestParam("value") String value, @RequestParam("status") final String status, @RequestParam("message") final String message, @RequestParam("location") final String location, @RequestParam(value = "param", required = false) final String param, final HttpServletRequest request) {
        final RestData restdata = new RestData();
        Map<String, String> dataMap = null;
        String context = null;
        restdata.setLocation("/order/edit");
        restdata.setParam(value);
        restdata.setStatus(201);
        restdata.setMessage("OK");
        try {
            dataMap = formatValidator.getValidationedMap(value, 8, new String[] { "sessionId", "token", "!title", "!context", "!iv", "!salt", "!passPhrase", "!itemRefCode" });
            context = deepValidator.getValidatedContext(dataMap.get("context"), dataMap.remove("iv"), dataMap.remove("salt"), dataMap.remove("passPhrase"));
            formatValidator.itemRefCodeValidation(dataMap.get("itemRefCode"));
        }
        catch (FormatDismatchException | ClassCastException | DecryptException ex2) {
            restdata.setStatus(400);
            restdata.setMessage("Bad request");
            return restdata;
        }
        catch (UnprocessableCodeException e2) {
            restdata.setStatus(451);
            restdata.setMessage("Unavailable For Legal Reasons");
            return restdata;
        }
        final ItemId itemId = new ItemId();
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
            restdata.setMessage("Service Unvailable");
            return restdata;
        }
        final String itemName = dataMap.remove("title");
        dataMap.remove("context");
        final String itemRefCode = dataMap.remove("itemRefCode");
        try {
            final String passId = this.loginService.authorization(dataMap, request);
            this.itemService.updateSortItem(itemName, context, passId, itemRefCode);
            restdata.setValue(true);
        }
        catch (PermissionException e4) {
            restdata.setStatus(422);
            restdata.setMessage("Unprocessable Entity");
        }
        catch (DatabaseException e5) {
            restdata.setStatus(503);
            restdata.setMessage("Service Unvailable");
            return restdata;
        }
        return restdata;
    }
    
    @GetMapping(EDIT_AUTH)
    public RestData checkEditAuth(@RequestParam("value") String value, @RequestParam("status") final String status, @RequestParam("message") final String message, @RequestParam("location") final String location, @RequestParam(value = "param", required = false) final String param, final HttpServletRequest request) {
        final RestData restdata = new RestData();
        Map<String, String> dataMap = null;
        restdata.setLocation("/order/edit/auth");
        restdata.setParam(value);
        restdata.setStatus(200);
        restdata.setMessage("OK");
        try {
            dataMap = formatValidator.getValidationedMap(value, 3, new String[] { "!sessionId", "token", "!itemRefCode" });
            formatValidator.itemRefCodeValidation(dataMap.get("itemRefCode"));
        }
        catch (FormatDismatchException e) {
            restdata.setStatus(400);
            restdata.setMessage("Bad request");
            return restdata;
        }
        final String itemRefCode = dataMap.remove("itemRefCode");
        try {
            final String passId = loginService.authorization(dataMap, request);
            if (itemService.authorization(passId, itemRefCode)) {
                restdata.setValue(itemService.getOrderContext(itemRefCode));
            }
        }
        catch (DatabaseException e2) {
            restdata.setStatus(503);
            restdata.setMessage("Service Unvailable");
        }
        catch (PermissionException e3) {
            restdata.setStatus(422);
            restdata.setMessage("Unprocessable Entity");
        }
        return restdata;
    }
    
    @GetMapping(EXECUTE_PATH)
    public RestData executeOrder(@RequestParam("value") String value, @RequestParam("status") final String status, @RequestParam("message") final String message, @RequestParam("location") final String location, @RequestParam(value = "param", required = false) final String param, final HttpServletRequest request) {
        final RestData restdata = new RestData();
        Map<String, String> dataMap = null;
        restdata.setLocation("/order/execute");
        restdata.setParam(value);
        restdata.setStatus(200);
        restdata.setMessage("OK");
        try {
            dataMap = formatValidator.getValidationedMap(value, 1, new String[] { "!itemRefCode" });
            formatValidator.itemRefCodeValidation(dataMap.get("itemRefCode"));
        }
        catch (FormatDismatchException e) {
            restdata.setStatus(400);
            restdata.setMessage("Bad request");
            return restdata;
        }
        try {
            final OrderExecuteMetadata executeMetadata = itemService.getOrderExecuteMetadata((String)dataMap.get("itemRefCode"));
            restdata.setValue((Object)executeMetadata);
        }
        catch (DatabaseException e2) {
            restdata.setStatus(503);
            restdata.setMessage("Service Unvailable");
        }
        return restdata;
    }
    
    @GetMapping(RECODE_PATH)
    public RestData recodeExecuteResult(@RequestParam("value") String value, @RequestParam("status") final String status, @RequestParam("message") final String message, @RequestParam("location") final String location, @RequestParam(value = "param", required = false) final String param, final HttpServletRequest request) {
        final RestData restdata = new RestData();
        Map<String, String> dataMap = null;
        restdata.setLocation("/order/execute/recode");
        restdata.setParam(value);
        restdata.setStatus(200);
        restdata.setMessage("OK");
        try {
            dataMap = formatValidator.getValidationedMap(value, 2, new String[] { "!itemRefCode", "!recode" });
            this.formatValidator.itemRefCodeValidation(dataMap.get("itemRefCode"));
            this.formatValidator.recodeValidation(dataMap.get("recode"));
        }
        catch (FormatDismatchException e) {
            restdata.setStatus(400);
            restdata.setMessage("Bad request");
            return restdata;
        }
        try {
            this.itemService.addItemUseRecode(dataMap.get("itemRefCode"), dataMap.get("recode"));
            final OrderExecuteMetadata metadata = itemService.getOrderExecuteMetadata(dataMap.get("itemRefCode"));
            restdata.setValue(metadata);
        }
        catch (DatabaseException e2) {
            restdata.setStatus(503);
            restdata.setMessage("Service Unvailable");
        }
        return restdata;
    }
}