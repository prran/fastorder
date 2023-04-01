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
    private static final String[] TARGETS;
    private final String[] release;
    
    static {
        TARGETS = new String[] { "'", "\"", "<", ">", "\\<", "\\>", "\\'", "\\\"", "&apos;", "&quot;", "&lt;", "&gt;", "&#96;&apos;", "&#96;&quot;", "&#96;&lt;", "&#96;&gt;", "%3c", "%3e", "%26%23x003C;", "%26%23x003E;" };
    }
    
    public RestDataXssValidator(final String... release) {
    	this.release = release;
    }
    
    public boolean supports(final Class<?> clazz) {
        return clazz.isAssignableFrom(Rest.class);
    }
    
    public void validate(final Object target, final Errors errors) {
        final Map<String, String> dataMap = (Map<String, String>)jsonService.parse(target);
        if (dataMap == null) {
            errors.reject(Message.FORMAT_REJECT.toString());
            return;
        }
        final Set<String> keys = dataMap.keySet();
        for (final String key : keys) {
            boolean next = false;
            String[] release;
            for (int length = (release = this.release).length, i = 0; i < length; ++i) {
                final String ban = release[i];
                if (ban.equals(key)) {
                    next = true;
                    break;
                }
            }
            if (next) {
                continue;
            }
            final String string = dataMap.get(key).trim().toLowerCase();
            String[] targets;
            for (int length2 = (targets = RestDataXssValidator.TARGETS).length, j = 0; j < length2; ++j) {
                final String str = targets[j];
                if (string.indexOf(str) != -1) {
                    errors.reject(Message.XSS_REJECT.toString());
                    break;
                }
            }
        }
    }
}