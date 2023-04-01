package com.kindlesstory.www.service.function;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import org.springframework.stereotype.Service;
import com.kindlesstory.www.service.inter.StringReformService;

@Service
public class JsonService implements StringReformService
{
    public Map<String, String> parse(final Object object) {
        try {
            @SuppressWarnings("unchecked")
			final Map<String, String> map = new ObjectMapper().readValue(object.toString(), Map.class);
            return map;
        }
        catch (Exception e) {
            return null;
        }
    }
}