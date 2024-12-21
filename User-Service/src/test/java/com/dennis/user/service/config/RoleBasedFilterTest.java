package com.dennis.user.service.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.mockito.Mockito.*;


class RoleBasedFilterTest {

    private RoleBasedFilter roleBasedFilter;

    @Mock
    private HttpServletRequest httpRequest;

    @Mock
    private HttpServletResponse httpResponse;

    @Mock
    private FilterChain filterChain;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        roleBasedFilter = new RoleBasedFilter();
    }

    @Test
    void shouldAllowAccessWhenRoleIsAdmin() throws Exception {
        when(httpRequest.getHeader("cross-roads")).thenReturn("ADMIN");

        roleBasedFilter.doFilter(httpRequest, httpResponse, filterChain);

        verify(filterChain, times(1)).doFilter(httpRequest, httpResponse);
        verify(httpResponse, never()).sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied: Admin role required");
    }

    @Test
    void shouldDenyAccessWhenRoleIsNotAdmin() throws Exception {
        when(httpRequest.getHeader("cross-roads")).thenReturn("USER");

        roleBasedFilter.doFilter(httpRequest, httpResponse, filterChain);

        verify(httpResponse, times(1)).sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied: Admin role required");
        verify(filterChain, never()).doFilter(httpRequest, httpResponse);
    }

    @Test
    void shouldDenyAccessWhenRoleHeaderIsMissing() throws Exception {
        when(httpRequest.getHeader("cross-roads")).thenReturn(null);

        roleBasedFilter.doFilter(httpRequest, httpResponse, filterChain);

        verify(httpResponse, times(1)).sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied: Admin role required");
        verify(filterChain, never()).doFilter(httpRequest, httpResponse);
    }
}
