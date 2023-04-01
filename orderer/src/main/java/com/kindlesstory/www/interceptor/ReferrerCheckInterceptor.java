package com.kindlesstory.www.interceptor;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.servlet.HandlerInterceptor;

public class ReferrerCheckInterceptor implements HandlerInterceptor
{
    public boolean preHandle(final HttpServletRequest request, final HttpServletResponse response, final Object handler) throws Exception {
        final String referer = request.getHeader("referer");
        final String host = request.getHeader("host");
        if (referer != null && !referer.contains(host.split(":")[0]) && !host.split(":")[0].equals("127.0.0.1")) {
            response.sendRedirect("/reject");
            return false;
        }
        return true;
    }
}