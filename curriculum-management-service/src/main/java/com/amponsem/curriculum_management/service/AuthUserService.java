package com.amponsem.curriculum_management.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Service
public class AuthUserService {

    public String getUserId() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            throw new IllegalStateException("No request context available");
        }

        HttpServletRequest request = attributes.getRequest();
        String userId = request.getHeader("X-User-Email");
        if (userId == null) {
            throw new IllegalArgumentException("Missing 'user' header");
        }
        return userId;
    }


    public String getUserRole() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            throw new IllegalStateException("No request context available");
        }

        HttpServletRequest request = attributes.getRequest();
        String role = request.getHeader("cross-roads");
        if (role == null) {
            throw new IllegalArgumentException("Missing 'user' header");
        }
        return role;
    }


}