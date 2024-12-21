package com.trainingmgt.live_quiz.service;

import com.trainingmgt.live_quiz.enums.Role;
import com.trainingmgt.live_quiz.util.JwtUtil;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;

@Service
public class AuthService {
    private final JwtUtil jwtUtil;

    public AuthService(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    private String getUserToken() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            throw new IllegalStateException("No request context available");
        }
        String userToken = attributes.getRequest().getHeader("X-Token");
        if (userToken == null) {
            throw new IllegalArgumentException("Missing token in header");
        }
        return userToken;
    }

    public String getUserId() {
        return jwtUtil.getClaims(getUserToken()).getSubject();
    }

    public Role getUserRole() {
        String roleString = jwtUtil.getClaims(getUserToken()).get("role", String.class);
        if (roleString == null) {
            throw new IllegalArgumentException("Role claim is missing in the token");
        }
        try {
            return Role.valueOf(roleString);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid role value in token: " + roleString, e);
        }
    }

    public boolean isAdmin() {
        return getUserRole().equals(Role.ADMIN);
    }

    public boolean authUserHasPermissionTo(String permission) {
        List<?> permissions = jwtUtil.getClaims(getUserToken()).get("permissions", List.class);
        return permissions != null && permissions.contains(permission);
    }
}
