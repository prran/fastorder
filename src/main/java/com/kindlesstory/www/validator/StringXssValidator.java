package com.kindlesstory.www.validator;

import com.kindlesstory.www.exception.PermissionException;
import com.kindlesstory.www.validator.multi.index.Message;
import org.springframework.validation.Errors;
import org.springframework.stereotype.Component;
import org.springframework.validation.Validator;

@Component
public class StringXssValidator implements Validator
{
    private static final String[] TARGETS = new String[] { "'", "\"", "<", ">", "\\<", "\\>", "\\'", "\\\"", "&apos;", "&quot;", "&lt;", "&gt;", "&#96;&apos;", "&#96;&quot;", "&#96;&lt;", "&#96;&gt;", "%3c", "%3e", "%26%23x003C;", "%26%23x003E;" };
    
    public boolean supports(Class<?> clazz) {
        return clazz.isAssignableFrom(String.class);
    }
    
    public void validate(Object target, Errors errors) {
        final String string = ((String)target).trim().toLowerCase();
        for (String str : TARGETS) {
            if (string.indexOf(str) != -1) {
                errors.reject(Message.XSS_REJECT.toString());
                break;
            }
        }
    }
    
    public void validate(String string) throws PermissionException {
        for (String str : TARGETS) {
            if (string.indexOf(str) != -1) {
                throw new PermissionException();
            }
        }
    }
}