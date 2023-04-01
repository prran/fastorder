package com.kindlesstory.www.validator.multi;

import com.kindlesstory.www.validator.multi.index.Message;
import org.springframework.validation.Errors;
import java.util.Iterator;
import java.util.Set;
import com.kindlesstory.www.exception.FormatDismatchException;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import com.kindlesstory.www.service.function.JsonService;
import org.springframework.stereotype.Component;

@Component
public class FormatValidator
{
    @Autowired
    JsonService jsonService;
    
    public Map<String, String> getValidationedMap(final Object jsonData, final int size, final String... keys) throws FormatDismatchException {
        final Map<String, String> map = jsonService.parse(jsonData);
        if (map == null) {
            throw new FormatDismatchException();
        }
        final int realSize = map.size();
        if (realSize > size || realSize < size) {
            throw new FormatDismatchException();
        }
        final Set<String> realkeys = map.keySet();
        for (final String realKey : realkeys) {
            boolean isMatched = false;
            for (String key : keys) {
                if (key.charAt(0) == '!') {
                    key = key.substring(1);
                }
                if (key.equals(realKey)) {
                    isMatched = true;
                    break;
                }
            }
            if (!isMatched) {
                throw new FormatDismatchException();
            }
        }
        for (String key2 : keys) {
            if (key2.charAt(0) == '!') {
                key2 = key2.substring(1);
                if (map.get(key2) == null || map.get(key2) == "") {
                    throw new FormatDismatchException();
                }
            }
        }
        return map;
    }
    
    public void itemRefCodeValidation(final String itemCode, final Errors errors) {
        try {
            final char[] itemCodeSequence = itemCode.toLowerCase().toCharArray();
            char[] array;
            for (int length = (array = itemCodeSequence).length, i = 0; i < length; ++i) {
                final char ch = array[i];
                if ((ch < 'a' || ch > 'z') && (ch < '0' || ch > '9')) {
                    throw new FormatDismatchException();
                }
            }
            if (itemCodeSequence.length > 32 || itemCodeSequence.length < 32) {
                throw new FormatDismatchException();
            }
        }
        catch (Exception e) {
            errors.reject(Message.FORMAT_REJECT.toString());
        }
    }
    
    public void itemRefCodeValidation(final String itemCode) throws FormatDismatchException {
        final char[] itemCodeSequence = itemCode.toLowerCase().toCharArray();
        char[] array;
        for (int length = (array = itemCodeSequence).length, i = 0; i < length; ++i) {
            final char ch = array[i];
            if ((ch < 'a' || ch > 'z') && (ch < '0' || ch > '9')) {
                throw new FormatDismatchException();
            }
        }
        if (itemCodeSequence.length > 32 || itemCodeSequence.length < 32) {
            throw new FormatDismatchException();
        }
    }
    
    public void recodeValidation(final String recode) throws FormatDismatchException {
        try {
            Long.parseLong(recode);
        }
        catch (NumberFormatException e) {
            throw new FormatDismatchException();
        }
    }
}