package com.kindlesstory.www.controller;

import com.kindlesstory.www.data.dto.OrderExecuteMetadata;
import com.kindlesstory.www.data.jpa.table.id.ItemId;
import com.kindlesstory.www.exception.UnprocessableCodeException;
import com.kindlesstory.www.exception.DecryptException;
import com.kindlesstory.www.data.dto.TokenSet;
import org.springframework.web.bind.annotation.PostMapping;
import com.kindlesstory.www.exception.OutcastUserException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import com.kindlesstory.www.exception.PermissionException;
import javax.servlet.http.HttpServletRequest;
import org.springframework.transaction.annotation.Transactional;
import java.util.Map;
import com.kindlesstory.www.exception.FormatDismatchException;
import org.springframework.web.bind.annotation.RequestParam;
import com.kindlesstory.www.data.dto.SimpleItem;
import com.kindlesstory.www.data.jpa.table.Item;
import com.kindlesstory.www.exception.DatabaseException;
import java.util.List;
import com.kindlesstory.www.module.Tester;
import com.kindlesstory.www.data.dto.MainViewComponentDto;
import java.util.ArrayList;
import com.kindlesstory.www.data.rest.RestData;
import org.springframework.web.bind.annotation.GetMapping;
import com.kindlesstory.www.validator.JavaScriptCodeDeepValidator;
import com.kindlesstory.www.validator.StringXssValidator;
import com.kindlesstory.www.validator.multi.FormatValidator;
import com.kindlesstory.www.service.model.UserService;
import com.kindlesstory.www.service.hybrid.LoginService;
import com.kindlesstory.www.service.function.JsonService;
import com.kindlesstory.www.service.model.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import com.kindlesstory.www.service.model.KategorieService;
import org.springframework.web.bind.annotation.RestController;

@SuppressWarnings("unused")
@RestController
public class PrototypeController
{
    @Autowired
    private KategorieService kategorieService;
    @Autowired
    private ItemService itemService;
    @Autowired
    private JsonService jsonService;
    @Autowired
    private LoginService loginService;
    @Autowired
    private UserService userService;
    @Autowired
    private FormatValidator formatValidator;
    @Autowired
    private StringXssValidator xssValidator;
    @Autowired
    private JavaScriptCodeDeepValidator deepValidator;
	private static final String TEST = "/test";
    private static final int PRE_PAGING = 2;
    private static final int PAGING = 1;
    private static final String PATH1 = "/test/main/component/information";
    private static final String PATH2 = "/test/main/paging";
    private static final String PATH3 = "/test/main/user/item";
    private static final String PATH4 = "/test/main/item/permission";
    private static final String PATH5 = "/test/main/item/remove";
    private static final String PATH6 = "/test/main/item/recovery";
    private static final String PATH7 = "/test/main/item/search";
    private static final String PATH_A = "/test/main/login";
    private static final String PATH_B = "/test/main/token";
    private static final String PATH_C = "/test/security/refresh-token";
    private static final String PATH_D = "/test/user/name";
    private static final String PATH_E = "/test/user/rename";
    private static final String PATH_F = "/test/order/create";
    private static final String PATH_G = "/test/order/execute";
    private static final String PATH_H = "/test/order/execute/recode";
    private static final String PATH_I = "/test/order/edit/auth";
    private static final String PATH_J = "/test/order/edit";
    
    @GetMapping({ "/test" })
    public String test() {
        System.out.println("log test");
        return "index";
    }
    
    @GetMapping({ "/test/main/component/information" })
    public RestData returnComponents() {
        final List<MainViewComponentDto> components = new ArrayList<MainViewComponentDto>();
        final RestData restdata = new RestData();
        restdata.setLocation("/test/main/component/information");
        restdata.setStatus(200);
        restdata.setMessage("OK");
        try {
            final List<String> kategorieList = (List<String>)this.kategorieService.getNames();
            for (final String kategorie : kategorieList) {
                final List<Item> items = (List<Item>)this.itemService.findItemsByKategorieName(kategorie, 0, 2);
                Tester.flag(88);
				final List<SimpleItem> simpleItems = (List<SimpleItem>)this.itemService.alterToSimpleItems(items);
                Tester.flag(91);
                final MainViewComponentDto component = new MainViewComponentDto();
                component.setKategorie(kategorie);
                component.setItems(simpleItems);
                components.add(component);
            }
            restdata.setValue((Object)components);
        }
        catch (DatabaseException e) {
            restdata.setStatus(503);
            restdata.setMessage("Service Unvailable");
            return restdata;
        }
        return restdata;
    }
    
	@GetMapping({ "/test/main/paging" })
    @Transactional
    public RestData paging(@RequestParam("value") String value, @RequestParam("status") final String status, @RequestParam("message") final String message, @RequestParam("location") final String location, @RequestParam(value = "param", required = false) final String param) {
        Tester.flag(130);
        final RestData restdata = new RestData();
        restdata.setLocation("/test/main/paging");
        restdata.setParam((Object)value);
        restdata.setStatus(200);
        restdata.setMessage("OK");
        Map<String, String> map = null;
        try {
            map = (Map<String, String>)this.formatValidator.getValidationedMap((Object)value, 3, new String[] { "!kategorie", "!lastItemCode", "searchLetter" });
            this.formatValidator.itemRefCodeValidation((String)map.get("lastItemCode"));
        }
        catch (FormatDismatchException e) {
            restdata.setStatus(400);
            restdata.setMessage("Bad request");
            return restdata;
        }
        try {
            final List<Item> items = (List<Item>)this.itemService.pagingByItemCode((String)map.get("kategorie"), (String)map.get("lastItemCode"), (String)map.get("searchLetter"), 1);
            final List<SimpleItem> simpleItems = (List<SimpleItem>)this.itemService.alterToSimpleItems(items);
            restdata.setValue((Object)simpleItems);
        }
        catch (DatabaseException e2) {
            restdata.setStatus(503);
            restdata.setMessage("Service Unvailable");
        }
        return restdata;
    }
    
    @GetMapping({ "/test/main/user/item" })
    public RestData returnUserItems(@RequestParam("value") String value, @RequestParam("status") final String status, @RequestParam("message") final String message, @RequestParam("location") final String location, @RequestParam(value = "param", required = false) final String param, final HttpServletRequest request) {
        Tester.flag(194);
        final RestData restdata = new RestData();
        restdata.setLocation("/test/main/user/item");
        restdata.setStatus(200);
        restdata.setMessage("OK");
        try {
            final Map<String, String> valueMap = (Map<String, String>)this.jsonService.parse((Object)value);
            final String passId = this.loginService.authorization(valueMap, request);
            System.out.println(passId);
            final List<Item> items = (List<Item>)this.itemService.findById(passId);
            final List<SimpleItem> simpleItems = (List<SimpleItem>)this.itemService.alterToSimpleItems(items);
            restdata.setValue((Object)simpleItems);
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
    
    @GetMapping({ "/test/main/item/permission" })
    public RestData checkItemAuth(@RequestParam("value") String value, @RequestParam("status") final String status, @RequestParam("message") final String message, @RequestParam("location") final String location, @RequestParam(value = "param", required = false) final String param, final HttpServletRequest request) {
        Tester.flag(245);
        System.out.println(value);
        final RestData restdata = new RestData();
        restdata.setLocation("/test/main/item/permission");
        restdata.setParam((Object)value);
        restdata.setStatus(200);
        restdata.setMessage("OK");
        try {
            final Map<String, String> map = (Map<String, String>)this.formatValidator.getValidationedMap((Object)value, 4, new String[] { "sessionId", "token", "password", "!itemRefCode" });
            this.formatValidator.itemRefCodeValidation((String)map.get("itemRefCode"));
        }
        catch (FormatDismatchException e2) {
            restdata.setStatus(400);
            restdata.setMessage("Bad request");
            return restdata;
        }
        try {
            final Map<String, String> jsonMap = (Map<String, String>)this.jsonService.parse((Object)value);
            final String encodePassword = jsonMap.remove("password");
            final String itemCode = jsonMap.remove("itemRefCode");
            boolean isPassed = false;
            Tester.flag(275);
            final String passId = this.loginService.authorization(jsonMap, request);
            isPassed = this.itemService.authorization(passId, itemCode);
            Tester.flag(282);
            if (!isPassed) {
                if (this.itemService.itemPasswordConfirm(itemCode, encodePassword)) {
                    this.itemService.addAuthorization(passId, itemCode);
                    restdata.setValue((Object)true);
                }
                else {
                    restdata.setValue((Object)false);
                }
            }
            else {
                restdata.setValue((Object)true);
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
    
    @GetMapping({ "/test/main/item/remove" })
    public RestData setItemDeathMode(@RequestParam("value") String value, @RequestParam("status") final String status, @RequestParam("message") final String message, @RequestParam("location") final String location, @RequestParam(value = "param", required = false) final String param, final HttpServletRequest request) {
        final RestData restdata = new RestData();
        restdata.setLocation("/test/main/item/remove");
        restdata.setParam((Object)value);
        restdata.setStatus(200);
        restdata.setMessage("OK");
        try {
            final Map<String, String> map = (Map<String, String>)this.formatValidator.getValidationedMap((Object)value, 3, new String[] { "!sessionId", "!token", "!itemRefCode" });
            this.formatValidator.itemRefCodeValidation((String)map.get("itemRefCode"));
        }
        catch (FormatDismatchException e2) {
            restdata.setStatus(400);
            restdata.setMessage("Bad request");
            return restdata;
        }
        try {
            final Map<String, String> jsonMap = (Map<String, String>)this.jsonService.parse((Object)value);
            final String itemCode = jsonMap.remove("itemRefCode");
            final String passId = this.loginService.authorization(jsonMap, request);
            boolean isPassed = false;
            isPassed = this.itemService.authorization(passId, itemCode);
            if (isPassed) {
                this.itemService.setDeathTime(itemCode);
                restdata.setValue((Object)true);
            }
            else {
                restdata.setValue((Object)false);
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
    
    @GetMapping({ "/test/main/item/recovery" })
    public RestData recoveryItemDeathMode(@RequestParam("value") String value, @RequestParam("status") final String status, @RequestParam("message") final String message, @RequestParam("location") final String location, @RequestParam(value = "param", required = false) final String param, final HttpServletRequest request) {
        final RestData restdata = new RestData();
        restdata.setLocation("/test/main/item/remove");
        restdata.setParam((Object)value);
        restdata.setStatus(200);
        restdata.setMessage("OK");
        try {
            final Map<String, String> map = (Map<String, String>)this.formatValidator.getValidationedMap((Object)value, 3, new String[] { "!sessionId", "!token", "!itemRefCode" });
            this.formatValidator.itemRefCodeValidation((String)map.get("itemRefCode"));
        }
        catch (FormatDismatchException e2) {
            restdata.setStatus(400);
            restdata.setMessage("Bad request");
            return restdata;
        }
        try {
            final Map<String, String> jsonMap = (Map<String, String>)this.jsonService.parse((Object)value);
            final String itemCode = jsonMap.remove("itemRefCode");
            final String passId = this.loginService.authorization(jsonMap, request);
            boolean isPassed = false;
            isPassed = this.itemService.authorization(passId, itemCode);
            if (isPassed) {
                this.itemService.removeDeathTime(itemCode);
                restdata.setValue((Object)true);
            }
            else {
                restdata.setValue((Object)false);
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
    
    @GetMapping({ "/test/main/item/search" })
    public RestData itemSearch(@RequestParam("value") String value, @RequestParam("status") final String status, @RequestParam("message") final String message, @RequestParam("location") final String location, @RequestParam(value = "param", required = false) final String param, final HttpServletRequest request) {
        final RestData restdata = new RestData();
        restdata.setLocation("/test/main/item/search");
        restdata.setStatus(200);
        restdata.setMessage("OK");
        restdata.setParam((Object)value);
        Map<String, String> map;
        try {
            map = (Map<String, String>)this.formatValidator.getValidationedMap((Object)value, 1, new String[] { "letter" });
        }
        catch (FormatDismatchException e) {
            restdata.setStatus(400);
            restdata.setMessage("Bad request");
            return restdata;
        }
        try {
            final String letter = map.get("letter");
            if (letter.equals("") || letter == null) {
                return this.returnComponents();
            }
            final List<String> kategorieList = (List<String>)this.kategorieService.getNames();
            final List<MainViewComponentDto> components = new ArrayList<MainViewComponentDto>();
            for (final String kategorie : kategorieList) {
                final List<Item> items = (List<Item>)this.itemService.findByItemNamePart(kategorie, (String)map.get("letter"), 2);
                final List<SimpleItem> simpleItems = (List<SimpleItem>)this.itemService.alterToSimpleItems(items);
                final MainViewComponentDto component = new MainViewComponentDto();
                component.setKategorie(kategorie);
                component.setItems(simpleItems);
                components.add(component);
            }
            restdata.setValue((Object)components);
        }
        catch (DatabaseException e2) {
            restdata.setStatus(503);
            restdata.setMessage("Service Unvailable");
        }
        return restdata;
    }
    
    public RestData exportPublickey() {
        return null;
    }
    
    public RestData createTokenId() {
        return null;
    }
    
    @PostMapping({ "/test/main/login" })
    public RestData login(@RequestBody final RestData postData, final BindingResult bindingResult) {
        final RestData restdata = new RestData();
        boolean hasAccount = false;
        restdata.setLocation("/test/main/login");
        restdata.setParam(postData.getValue());
        restdata.setStatus(201);
        restdata.setMessage("OK");
        try {
            hasAccount = this.loginService.authentication(postData.getValue());
        }
        catch (OutcastUserException e) {
            restdata.setStatus(422);
            restdata.setMessage("Unprocessable Entity");
            return restdata;
        }
        catch (FormatDismatchException e2) {
            restdata.setStatus(400);
            restdata.setMessage("Bad request");
            return restdata;
        }
        catch (DatabaseException e3) {
            restdata.setStatus(503);
            restdata.setMessage("Service Unvailable");
            return restdata;
        }
        if (!hasAccount) {
            try {
                this.loginService.createAccount(postData.getValue());
            }
            catch (FormatDismatchException e4) {
                restdata.setStatus(400);
                restdata.setMessage("Bad request");
            }
            catch (DatabaseException e5) {
                restdata.setStatus(503);
                restdata.setMessage("Service Unvailable");
            }
        }
        return restdata;
    }
    
    @PostMapping({ "/test/main/token" })
    public RestData tokenLogin(@RequestBody final RestData postData) {
        Tester.flag(588);
        System.out.println(postData.getValue());
        final RestData restdata = new RestData();
        restdata.setLocation("/test/main/token");
        restdata.setParam(postData.getValue());
        restdata.setStatus(200);
        restdata.setMessage("OK");
        try {
            final boolean hasAccount = this.loginService.authentication(postData.getValue());
            if (!hasAccount) {
                restdata.setStatus(418);
                restdata.setMessage("Request reject");
                return restdata;
            }
            Tester.flag(610);
            final TokenSet tokenSet = this.loginService.getToken(postData.getValue());
            restdata.setValue((Object)tokenSet);
        }
        catch (OutcastUserException e) {
            restdata.setStatus(422);
            restdata.setMessage("Unprocessable Entity");
        }
        catch (FormatDismatchException e2) {
            restdata.setStatus(400);
            restdata.setMessage("Bad request");
        }
        catch (DatabaseException e3) {
            restdata.setStatus(503);
            restdata.setMessage("Service Unvailable");
            restdata.setValue((Object)"error");
        }
        return restdata;
    }
    
    @PostMapping({ "/test/security/refresh-token" })
    public RestData refreshToken(@RequestBody final RestData postData) {
        final RestData restdata = new RestData();
        Map<String, String> map = null;
        restdata.setLocation("/test/security/refresh-token");
        restdata.setParam(postData.getValue());
        restdata.setStatus(201);
        restdata.setMessage("OK");
        try {
            map = (Map<String, String>)this.formatValidator.getValidationedMap(postData.getValue(), 2, new String[] { "!refreshToken", "!accessToken" });
        }
        catch (FormatDismatchException e) {
            restdata.setStatus(400);
            restdata.setMessage("Bad request");
            return restdata;
        }
        try {
            Tester.flag(659);
            final String newAccessToken = this.loginService.refreshToken(map);
            restdata.setValue((Object)newAccessToken);
        }
        catch (PermissionException e2) {
            restdata.setStatus(422);
            restdata.setMessage("Unprocessable Entity");
        }
        return restdata;
    }
    
    @GetMapping({ "/test/user/name" })
    public RestData getUserName(@RequestParam("value") String value, @RequestParam("status") final String status, @RequestParam("message") final String message, @RequestParam("location") final String location, @RequestParam(value = "param", required = false) final String param, final HttpServletRequest request) {
        Tester.flag(691);
        final RestData restdata = new RestData();
        Map<String, String> map = null;
        restdata.setLocation("/test/user/name");
        restdata.setParam((Object)value);
        restdata.setStatus(200);
        restdata.setMessage("OK");
        try {
            map = (Map<String, String>)this.formatValidator.getValidationedMap((Object)value, 2, new String[] { "sessionId", "token" });
        }
        catch (FormatDismatchException e) {
            restdata.setStatus(400);
            restdata.setMessage("Bad request");
            return restdata;
        }
        try {
            final String passId = this.loginService.authorization(map, request);
            final String userName = this.userService.getName(passId);
            restdata.setValue((Object)userName);
        }
        catch (PermissionException e2) {
            restdata.setStatus(422);
            restdata.setMessage("Unprocessable Entity");
        }
        catch (DatabaseException e3) {
            restdata.setStatus(503);
            restdata.setMessage("Service Unvailable");
        }
        return restdata;
    }
    
    @GetMapping({ "/test/user/rename" })
    public RestData rename(@RequestParam("value") String value, @RequestParam("status") final String status, @RequestParam("message") final String message, @RequestParam("location") final String location, @RequestParam(value = "param", required = false) final String param, final HttpServletRequest request) {
        final RestData restdata = new RestData();
        Map<String, String> map = null;
        restdata.setLocation("/test/user/rename");
        restdata.setParam((Object)value);
        restdata.setStatus(201);
        restdata.setMessage("OK");
        try {
            map = (Map<String, String>)this.formatValidator.getValidationedMap((Object)value, 3, new String[] { "sessionId", "token", "!newName" });
            this.xssValidator.validate((String)map.get("newName"));
        }
        catch (FormatDismatchException e2) {
            restdata.setStatus(400);
            restdata.setMessage("Bad request");
            return restdata;
        }
        catch (PermissionException e3) {
            restdata.setStatus(422);
            restdata.setMessage("Unprocessable Entity");
        }
        try {
            final String newName = map.remove("newName");
            final String passId = this.loginService.authorization(map, request);
            restdata.setValue((Object)this.userService.setName(passId, newName));
        }
        catch (FormatDismatchException | NullPointerException ex2) {
            restdata.setStatus(400);
            restdata.setMessage("Bad request");
        }
        catch (PermissionException e4) {
            restdata.setStatus(422);
            restdata.setMessage("Unprocessable Entity");
        }
        catch (DatabaseException e5) {
            restdata.setStatus(503);
            restdata.setMessage("Service Unvailable");
        }
        return restdata;
    }
    
    @GetMapping({ "/test/order/create" })
    public RestData createOrder(@RequestParam("value") String value, @RequestParam("status") final String status, @RequestParam("message") final String message, @RequestParam("location") final String location, @RequestParam(value = "param", required = false) final String param, final HttpServletRequest request) {
        final RestData restdata = new RestData();
        Map<String, String> dataMap = null;
        String context = null;
        restdata.setLocation("/test/order/create");
        restdata.setParam((Object)value);
        restdata.setStatus(201);
        restdata.setMessage("OK");
        try {
            dataMap = (Map<String, String>)this.formatValidator.getValidationedMap((Object)value, 8, new String[] { "sessionId", "token", "password", "!title", "!context", "!iv", "!salt", "!passPhrase" });
            context = this.deepValidator.getValidatedContext((String)dataMap.get("context"), (String)dataMap.remove("iv"), (String)dataMap.remove("salt"), (String)dataMap.remove("passPhrase"));
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
        itemId.setItemName((String)dataMap.get("title"));
        itemId.setKateName("\uc815\ub82c");
        try {
            if (this.itemService.existByPrimaryKey(itemId)) {
                restdata.setMessage("multiple index value");
                restdata.setValue((Object)false);
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
            final String passId = this.loginService.authorization(dataMap, request);
            this.itemService.insertSortItem(passId, itemName, context, password);
            restdata.setValue((Object)true);
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
    
    @GetMapping({ "/test/order/execute" })
    public RestData executeOrder(@RequestParam("value") String value, @RequestParam("status") final String status, @RequestParam("message") final String message, @RequestParam("location") final String location, @RequestParam(value = "param", required = false) final String param, final HttpServletRequest request) {
        final RestData restdata = new RestData();
        Map<String, String> dataMap = null;
        restdata.setLocation("/test/order/execute");
        restdata.setParam((Object)value);
        restdata.setStatus(200);
        restdata.setMessage("OK");
        try {
            dataMap = (Map<String, String>)this.formatValidator.getValidationedMap((Object)value, 1, new String[] { "!itemRefCode" });
            this.formatValidator.itemRefCodeValidation((String)dataMap.get("itemRefCode"));
        }
        catch (FormatDismatchException e) {
            restdata.setStatus(400);
            restdata.setMessage("Bad request");
            return restdata;
        }
        try {
            final OrderExecuteMetadata executeMetadata = this.itemService.getOrderExecuteMetadata((String)dataMap.get("itemRefCode"));
            restdata.setValue((Object)executeMetadata);
        }
        catch (DatabaseException e2) {
            restdata.setStatus(503);
            restdata.setMessage("Service Unvailable");
        }
        return restdata;
    }
    
    @GetMapping({ "/test/order/execute/recode" })
    public RestData recodeExecuteResult(@RequestParam("value") String value, @RequestParam("status") final String status, @RequestParam("message") final String message, @RequestParam("location") final String location, @RequestParam(value = "param", required = false) final String param, final HttpServletRequest request) {
        final RestData restdata = new RestData();
        Map<String, String> dataMap = null;
        restdata.setLocation("/test/order/execute/recode");
        restdata.setParam((Object)value);
        restdata.setStatus(200);
        restdata.setMessage("OK");
        try {
            dataMap = (Map<String, String>)this.formatValidator.getValidationedMap((Object)value, 2, new String[] { "!itemRefCode", "!recode" });
            this.formatValidator.itemRefCodeValidation((String)dataMap.get("itemRefCode"));
            this.formatValidator.recodeValidation((String)dataMap.get("recode"));
        }
        catch (FormatDismatchException e) {
            restdata.setStatus(400);
            restdata.setMessage("Bad request");
            return restdata;
        }
        try {
            this.itemService.addItemUseRecode((String)dataMap.get("itemRefCode"), (String)dataMap.get("recode"));
            final OrderExecuteMetadata metadata = this.itemService.getOrderExecuteMetadata((String)dataMap.get("itemRefCode"));
            restdata.setValue((Object)metadata);
        }
        catch (DatabaseException e2) {
            restdata.setStatus(503);
            restdata.setMessage("Service Unvailable");
        }
        return restdata;
    }
    
    @GetMapping({ "/test/order/edit/auth" })
    public RestData checkEditAuth(@RequestParam("value") String value, @RequestParam("status") final String status, @RequestParam("message") final String message, @RequestParam("location") final String location, @RequestParam(value = "param", required = false) final String param, final HttpServletRequest request) {
        final RestData restdata = new RestData();
        Map<String, String> dataMap = null;
        restdata.setLocation("/test/order/edit/auth");
        restdata.setParam((Object)value);
        restdata.setStatus(200);
        restdata.setMessage("OK");
        try {
            dataMap = (Map<String, String>)this.formatValidator.getValidationedMap((Object)value, 3, new String[] { "!sessionId", "token", "!itemRefCode" });
            this.formatValidator.itemRefCodeValidation((String)dataMap.get("itemRefCode"));
        }
        catch (FormatDismatchException e) {
            restdata.setStatus(400);
            restdata.setMessage("Bad request");
            return restdata;
        }
        final String itemRefCode = dataMap.remove("itemRefCode");
        try {
            final String passId = this.loginService.authorization(dataMap, request);
            if (this.itemService.authorization(passId, itemRefCode)) {
                restdata.setValue((Object)this.itemService.getOrderContext(itemRefCode));
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
    
    @GetMapping({ "/test/order/edit" })
    public RestData editOrder(@RequestParam("value") String value, @RequestParam("status") final String status, @RequestParam("message") final String message, @RequestParam("location") final String location, @RequestParam(value = "param", required = false) final String param, final HttpServletRequest request) {
        Tester.flag(1054);
        final RestData restdata = new RestData();
        Map<String, String> dataMap = null;
        String context = null;
        restdata.setLocation("/test/order/edit");
        restdata.setParam((Object)value);
        restdata.setStatus(201);
        restdata.setMessage("OK");
        try {
            dataMap = (Map<String, String>)this.formatValidator.getValidationedMap((Object)value, 8, new String[] { "sessionId", "token", "!title", "!context", "!iv", "!salt", "!passPhrase", "!itemRefCode" });
            context = this.deepValidator.getValidatedContext((String)dataMap.get("context"), (String)dataMap.remove("iv"), (String)dataMap.remove("salt"), (String)dataMap.remove("passPhrase"));
            this.formatValidator.itemRefCodeValidation((String)dataMap.get("itemRefCode"));
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
        itemId.setItemName((String)dataMap.get("title"));
        itemId.setKateName("\uc815\ub82c");
        try {
            if (!this.itemService.qualsItemNameByItemRefCode((String)dataMap.get("title"), (String)dataMap.get("itemRefCode")) && this.itemService.existByPrimaryKey(itemId)) {
                restdata.setMessage("multiple index value");
                restdata.setValue((Object)false);
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
            restdata.setValue((Object)true);
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
}