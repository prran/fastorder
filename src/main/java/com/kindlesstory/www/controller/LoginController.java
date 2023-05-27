package com.kindlesstory.www.controller;

import javax.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.RequestParam;
import com.kindlesstory.www.exception.PermissionException;
import java.util.Map;
import com.kindlesstory.www.data.dto.TokenSet;
import org.springframework.web.bind.annotation.PostMapping;
import com.kindlesstory.www.exception.DatabaseException;
import com.kindlesstory.www.exception.FormatDismatchException;
import com.kindlesstory.www.exception.OutcastUserException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;

import com.kindlesstory.www.data.rest.Rest;
import com.kindlesstory.www.data.rest.RestData;
import com.kindlesstory.www.validator.StringXssValidator;
import com.kindlesstory.www.validator.multi.FormatValidator;
import com.kindlesstory.www.module.Crypt;
import com.kindlesstory.www.service.model.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import com.kindlesstory.www.service.hybrid.LoginService;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.context.annotation.Profile;
import com.kindlesstory.www.data.cast.UseAuth;

@Profile({ "real" })
@RestController
public class LoginController extends UseAuth
{
    private final static String LOGIN_PATH = "/main/login";
    private final static String TOKEN_PATH = "/main/token";
    private final static String NAMING_PATH = "/user/name";
    private final static String RENAMING_PATH = "/user/rename";
    private final static String PUBLIC_KEY_PATH = "/security/public-key";
    private final static String TOKEN_REFRESH_PATH = "/security/refresh-token";
    private final static String TOKEN_SIGN_UP = "/security/token/sign-up";
    @Autowired
    private LoginService loginService;
    @Autowired
    private UserService userService;
    @Autowired
    private Crypt crypt;
    @Autowired
    private FormatValidator formatValidator;
    @Autowired
    private StringXssValidator xssValidator;
    
    @GetMapping(PUBLIC_KEY_PATH)
    public RestData exportPublickey() {
        RestData restdata = new RestData();
        restdata.setLocation(PUBLIC_KEY_PATH);
        restdata.setStatus(200);
        restdata.setMessage(Rest.OK);
        restdata.setValue( crypt.getPublicKey());
        return restdata;
    }
    
    @PostMapping(LOGIN_PATH)
    public RestData login(@RequestBody RestData postData, BindingResult bindingResult) {
        RestData restdata = new RestData();
        boolean hasAccount = false;
        restdata.setLocation(LOGIN_PATH);
        restdata.setParam(postData.getValue());
        restdata.setStatus(201);
        restdata.setMessage(Rest.OK);
        try {
            hasAccount = this.loginService.authentication(postData.getValue());
        }
        catch (OutcastUserException e) {
            restdata.setStatus(422);
            restdata.setMessage(Rest.UNPROCESSABLE_ENTITY);
            return restdata;
        }
        catch (FormatDismatchException e2) {
            restdata.setStatus(400);
            restdata.setMessage(Rest.BAD_REQUEST);
            return restdata;
        }
        catch (DatabaseException e3) {
            restdata.setStatus(503);
            restdata.setMessage(Rest.SERVICE_UNVAILABLE);
            return restdata;
        }
        if (!hasAccount) {
            try {
                this.loginService.createAccount(postData.getValue());
            }
            catch (FormatDismatchException e4) {
                restdata.setStatus(400);
                restdata.setMessage(Rest.BAD_REQUEST);
            }
            catch (DatabaseException e5) {
                restdata.setStatus(503);
                restdata.setMessage(Rest.SERVICE_UNVAILABLE);
            }
        }
        return restdata;
    }
    
    @GetMapping(TOKEN_SIGN_UP)
    public RestData createTokenId() {
        RestData restdata = new RestData();
        restdata.setLocation(TOKEN_SIGN_UP);
        restdata.setStatus(200);
        restdata.setMessage("OK");
        try {
            String encodeMiddleId = loginService.createTokenAccount();
            restdata.setValue( encodeMiddleId);
        }
        catch (DatabaseException e2) {
            restdata.setStatus(503);
            restdata.setMessage(Rest.SERVICE_UNVAILABLE);
        }
        return restdata;
    }
    
    @PostMapping(TOKEN_PATH)
    public RestData tokenLogin(@RequestBody RestData postData) {
        RestData restdata = new RestData();
        restdata.setLocation(TOKEN_PATH);
        restdata.setParam(postData.getValue());
        restdata.setStatus(200);
        restdata.setMessage("OK");
        try {
            boolean hasAccount = loginService.authentication(postData.getValue());
            if (!hasAccount) {
                restdata.setStatus(418);
                restdata.setMessage(Rest.IM_TEAPOT);
                return restdata;
            }
            TokenSet tokenSet = loginService.getToken(postData.getValue());
            restdata.setValue( tokenSet);
        }
        catch (OutcastUserException e) {
            restdata.setStatus(422);
            restdata.setMessage(Rest.UNPROCESSABLE_ENTITY);
        }
        catch (FormatDismatchException e2) {
            restdata.setStatus(400);
            restdata.setMessage(Rest.BAD_REQUEST);
        }
        catch (DatabaseException e3) {
            restdata.setStatus(503);
            restdata.setMessage(Rest.SERVICE_UNVAILABLE);
            restdata.setValue( "error");
        }
        return restdata;
    }
    
    @PostMapping(TOKEN_REFRESH_PATH)
    public RestData refreshToken(@RequestBody RestData postData) {
        RestData restdata = new RestData();
        Map<String, String> map = null;
        restdata.setLocation(TOKEN_REFRESH_PATH);
        restdata.setParam(postData.getValue());
        restdata.setStatus(201);
        restdata.setMessage("OK");
        try {
            map = formatValidator.getValidationedMap(postData.getValue(), 2, "!refreshToken", "!accessToken");
        }
        catch (FormatDismatchException e) {
            restdata.setStatus(400);
            restdata.setMessage(Rest.BAD_REQUEST);
            return restdata;
        }
        try {
            String newAccessToken = loginService.refreshToken(map);
            restdata.setValue( newAccessToken);
        }
        catch (PermissionException e2) {
            restdata.setStatus(422);
            restdata.setMessage(Rest.UNPROCESSABLE_ENTITY);
        }
        return restdata;
    }
    
    @GetMapping(NAMING_PATH)
    public RestData getUserName(@RequestParam("value") String value, 
    							@RequestParam("status") String status, 
    							@RequestParam("message") String message, 
    							@RequestParam("location") String location, 
    							@RequestParam(value = "param", required = false) 
    							String param, HttpServletRequest request) {
        RestData restdata = new RestData();
        Map<String, String> map = null;
        restdata.setLocation(NAMING_PATH);
        restdata.setParam(value);
        restdata.setStatus(200);
        restdata.setMessage("OK");
        try {
            map = (Map<String, String>)formatValidator.getValidationedMap(value, 2, "sessionId", "token");
        }
        catch (FormatDismatchException e) {
            restdata.setStatus(400);
            restdata.setMessage(Rest.BAD_REQUEST);
            return restdata;
        }
        try {
            String passId = loginService.authorization(map, request);
            String userName = userService.getName(passId);
            restdata.setValue( userName);
        }
        catch (PermissionException e2) {
            restdata.setStatus(422);
            restdata.setMessage(Rest.UNPROCESSABLE_ENTITY);
        }
        catch (DatabaseException e3) {
            restdata.setStatus(503);
            restdata.setMessage(Rest.SERVICE_UNVAILABLE);
        }
        return restdata;
    }
    
    @GetMapping(RENAMING_PATH)
    public RestData rename(@RequestParam("value") String value, 
    						@RequestParam("status") String status, 
    						@RequestParam("message") String message, 
    						@RequestParam("location") String location, 
    						@RequestParam(value = "param", required = false) 
    						String param, HttpServletRequest request) {
        RestData restdata = new RestData();
        Map<String, String> map = null;
        restdata.setLocation(RENAMING_PATH);
        restdata.setParam(value);
        restdata.setStatus(201);
        restdata.setMessage("OK");
        try {
            map = this.formatValidator.getValidationedMap(value, 3, "sessionId", "token", "!newName");
            this.xssValidator.validate(map.get("newName"));
        }
        catch (FormatDismatchException e2) {
            restdata.setStatus(400);
            restdata.setMessage(Rest.BAD_REQUEST);
            return restdata;
        }
        catch (PermissionException e3) {
            restdata.setStatus(422);
            restdata.setMessage(Rest.UNPROCESSABLE_ENTITY);
        }
        try {
            String newName = map.remove("newName");
            String passId = loginService.authorization(map, request);
            restdata.setValue(userService.setName(passId, newName));
        }
        catch (FormatDismatchException | NullPointerException ex2) {
            restdata.setStatus(400);
            restdata.setMessage(Rest.BAD_REQUEST);
        }
        catch (PermissionException e4) {
            restdata.setStatus(422);
            restdata.setMessage(Rest.UNPROCESSABLE_ENTITY);
        }
        catch (DatabaseException e5) {
            restdata.setStatus(503);
            restdata.setMessage(Rest.SERVICE_UNVAILABLE);
        }
        return restdata;
    }
}