package com.kindlesstory.www.interceptor;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.servlet.HandlerInterceptor;

public class ReferrerCheckInterceptor implements HandlerInterceptor
{
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String referer = request.getHeader("referer");
        if (referer != null 
        		&& !referer.contains("https://arlgorithm.kro.kr")) {
            response.sendRedirect("/reject");
            return false;
        }
        return true;
    }
}