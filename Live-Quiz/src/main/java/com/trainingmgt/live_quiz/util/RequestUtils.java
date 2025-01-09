package com.trainingmgt.live_quiz.util;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class RequestUtils {

    public static HttpServletRequest getCurrentHttpRequest() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes instanceof ServletRequestAttributes) {
            return ((ServletRequestAttributes) requestAttributes).getRequest();
        }
        throw new IllegalStateException("No current HTTP request available");
    }

    public static String getHeader(HttpServletRequest request, String headerName) {
        return request.getHeader(headerName);
    }

    public static String getUserId() {
        return getHeader(getCurrentHttpRequest(), "X-User-Email");
    }

    public static String getUserRole() {
        return getHeader(getCurrentHttpRequest(), "cross-roads");
    }

    public static String getJwtToken() {
        return getHeader(getCurrentHttpRequest(), "X-TOKEN");
    }

    public static String getRequestUri() {
        return getCurrentHttpRequest().getRequestURI().substring(getCurrentHttpRequest().getContextPath().length());

         }
    public static String getRequestPath() {
        return getCurrentHttpRequest().getRequestURI().substring(getCurrentHttpRequest().getContextPath().length());
    }

}
