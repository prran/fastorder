package com.kindlesstory.www.service.function;

import java.util.Iterator;
import java.util.List;
import com.kindlesstory.www.validator.multi.index.Message;
import org.springframework.validation.ObjectError;
import com.kindlesstory.www.data.rest.RestData;
import org.springframework.validation.Errors;
import org.springframework.stereotype.Service;
import com.kindlesstory.www.service.inter.ValidationService;

@Service
public class ValidateSupportService implements ValidationService
{
    public RestData makeRestData(Errors bindingError, RestData restdata) {
        final List<ObjectError> errors = bindingError.getAllErrors();
        final Iterator<ObjectError> iterator = errors.iterator();
        if (iterator.hasNext()) {
            final ObjectError error = iterator.next();
            final Message message = Message.valueOf(error.getDefaultMessage());
            switch (message) {
                case FORMAT_REJECT:
                case TYPE_REJECT: {
                    restdata.setStatus(400);
                    restdata.setMessage("Bad request");
                    break;
                }
                case XSS_REJECT:
                case UNPROCESSABLE_CODE_REJECT: {
                    restdata.setStatus(422);
                    restdata.setMessage("Unprocessable Entity");
                    break;
                }
            }
            return restdata;
        }
        restdata.setStatus(444);
        restdata.setMessage("AN UNDEFINED FATAL ERROR OCCURED!! please check server!!");
        return restdata;
    }
}