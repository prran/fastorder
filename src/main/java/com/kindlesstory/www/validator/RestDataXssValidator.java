package com.kindlesstory.www.validator;

import java.util.Set;
import java.util.Map;
import com.kindlesstory.www.validator.multi.index.Message;
import org.springframework.validation.Errors;
import com.kindlesstory.www.data.rest.Rest;
import org.springframework.beans.factory.annotation.Autowired;
import com.kindlesstory.www.service.function.JsonService;
import org.springframework.validation.Validator;

public class RestDataXssValidator implements Validator
{
    @Autowired
    private JsonService jsonService;
    private final static String[] TARGETS = new String[] { "'", "\"", "<", ">", "\\<", "\\>", "\\'", "\\\"", "&apos;", "&quot;", "&lt;", "&gt;", "&#96;&apos;", "&#96;&quot;", "&#96;&lt;", "&#96;&gt;", "%3c", "%3e", "%26%23x003C;", "%26%23x003E;" };;
    private String[] release;
    
    public RestDataXssValidator(String... release) {
    	this.release = release;
    }
    
    public boolean supports(Class<?> clazz) {
        return clazz.isAssignableFrom(Rest.class);
    }
    
    public void validate(Object target, Errors errors) {
        Map<String, String> dataMap = (Map<String, String>)jsonService.parse(target);
        if (dataMap == null) {
            errors.reject(Message.FORMAT_REJECT.toString());
            return;
        }
        Set<String> keys = dataMap.keySet();
        for (String key : keys) {
            boolean next = false;
            String[] release;
            for (int length = (release = this.release).length, i = 0; i < length; ++i) {
                String ban = release[i];
                if (ban.equals(key)) {
                    next = true;
                    break;
                }
            }
            if (next) {
                continue;
            }
            String string = dataMap.get(key).trim().toLowerCase();
            for (String str : TARGETS) {
                if (string.indexOf(str) != -1) {
                    errors.reject(Message.XSS_REJECT.toString());
                    break;
                }
            }
        }
    }
}