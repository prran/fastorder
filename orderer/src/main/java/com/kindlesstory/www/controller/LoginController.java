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
    private static final String LOGIN_PATH = "/main/login";
    private static final String TOKEN_PATH = "/main/token";
    private static final String NAMING_PATH = "/user/name";
    private static final String RENAMING_PATH = "/user/rename";
    private static final String PUBLIC_KEY_PATH = "/security/public-key";
    private static final String TOKEN_REFRESH_PATH = "/security/refresh-token";
    private static final String TOKEN_SIGN_UP = "/security/token/sign-up";
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
        final RestData restdata = new RestData();
        restdata.setLocation("/security/public-key");
        restdata.setStatus(200);
        restdata.setMessage("OK");
        restdata.setValue( crypt.getPublicKey());
        return restdata;
    }
    
    @PostMapping(LOGIN_PATH)
    public RestData login(@RequestBody final RestData postData, final BindingResult bindingResult) {
        final RestData restdata = new RestData();
        boolean hasAccount = false;
        restdata.setLocation("/main/login");
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
    
    @GetMapping(TOKEN_SIGN_UP)
    public RestData createTokenId() {
        final RestData restdata = new RestData();
        restdata.setLocation("/security/token/sign-up");
        restdata.setStatus(200);
        restdata.setMessage("OK");
        try {
            final String encodeMiddleId = loginService.createTokenAccount();
            restdata.setValue( encodeMiddleId);
        }
        catch (DatabaseException e2) {
            restdata.setStatus(503);
            restdata.setMessage("Service Unvailable");
        }
        return restdata;
    }
    
    @PostMapping(TOKEN_PATH)
    public RestData tokenLogin(@RequestBody final RestData postData) {
        final RestData restdata = new RestData();
        restdata.setLocation("/main/token");
        restdata.setParam(postData.getValue());
        restdata.setStatus(200);
        restdata.setMessage("OK");
        try {
            final boolean hasAccount = loginService.authentication(postData.getValue());
            if (!hasAccount) {
                restdata.setStatus(418);
                restdata.setMessage("Request reject");
                return restdata;
            }
            final TokenSet tokenSet = loginService.getToken(postData.getValue());
            restdata.setValue( tokenSet);
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
            restdata.setValue( "error");
        }
        return restdata;
    }
    
    @PostMapping(TOKEN_REFRESH_PATH)
    public RestData refreshToken(@RequestBody final RestData postData) {
        final RestData restdata = new RestData();
        Map<String, String> map = null;
        restdata.setLocation("/security/refresh-token");
        restdata.setParam(postData.getValue());
        restdata.setStatus(201);
        restdata.setMessage("OK");
        try {
            map = formatValidator.getValidationedMap(postData.getValue(), 2, new String[] { "!refreshToken", "!accessToken" });
        }
        catch (FormatDismatchException e) {
            restdata.setStatus(400);
            restdata.setMessage("Bad request");
            return restdata;
        }
        try {
            final String newAccessToken = loginService.refreshToken(map);
            restdata.setValue( newAccessToken);
        }
        catch (PermissionException e2) {
            restdata.setStatus(422);
            restdata.setMessage("Unprocessable Entity");
        }
        return restdata;
    }
    
    @GetMapping(NAMING_PATH)
    public RestData getUserName(@RequestParam("value") String value, @RequestParam("status") final String status, @RequestParam("message") final String message, @RequestParam("location") final String location, @RequestParam(value = "param", required = false) final String param, final HttpServletRequest request) {
        final RestData restdata = new RestData();
        Map<String, String> map = null;
        restdata.setLocation("/user/name");
        restdata.setParam(value);
        restdata.setStatus(200);
        restdata.setMessage("OK");
        try {
            map = (Map<String, String>)formatValidator.getValidationedMap(value, 2, new String[] { "sessionId", "token" });
        }
        catch (FormatDismatchException e) {
            restdata.setStatus(400);
            restdata.setMessage("Bad request");
            return restdata;
        }
        try {
            final String passId = loginService.authorization(map, request);
            final String userName = userService.getName(passId);
            restdata.setValue( userName);
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
    
    @GetMapping(RENAMING_PATH)
    public RestData rename(@RequestParam("value") String value, @RequestParam("status") final String status, @RequestParam("message") final String message, @RequestParam("location") final String location, @RequestParam(value = "param", required = false) final String param, final HttpServletRequest request) {
        final RestData restdata = new RestData();
        Map<String, String> map = null;
        restdata.setLocation("/user/rename");
        restdata.setParam(value);
        restdata.setStatus(201);
        restdata.setMessage("OK");
        try {
            map = this.formatValidator.getValidationedMap( value, 3, new String[] { "sessionId", "token", "!newName" });
            this.xssValidator.validate(map.get("newName"));
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
            final String passId = loginService.authorization(map, request);
            restdata.setValue(userService.setName(passId, newName));
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
}