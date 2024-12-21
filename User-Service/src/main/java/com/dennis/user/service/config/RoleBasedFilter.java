package com.dennis.user.service.config;


import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class RoleBasedFilter implements Filter {
    private static final String ROLE_HEADER = "cross-roads";
    private static final String ADMIN_ROLE = "ADMIN";

    @Override
    public void doFilter(jakarta.servlet.ServletRequest request, jakarta.servlet.ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // Check for role in header
        String role = httpRequest.getHeader(ROLE_HEADER);

        if (ADMIN_ROLE.equalsIgnoreCase(role)) {
            // Proceed to the next filter or controller if the role is ADMIN
            chain.doFilter(request, response);
        } else {
            // Reject the request if the role is not ADMIN
            httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied: Admin role required");
        }
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void destroy() {

    }
}
