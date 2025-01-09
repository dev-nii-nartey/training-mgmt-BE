package com.dennis.assessment_service.config;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;

public class CustomAuthentication extends AbstractAuthenticationToken {

    private final String role;

    public CustomAuthentication(String role) {
        super(Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role)));
        this.role = role;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return role;
    }
}
