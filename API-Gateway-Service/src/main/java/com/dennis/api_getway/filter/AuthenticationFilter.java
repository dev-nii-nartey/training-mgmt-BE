package com.dennis.api_getway.filter;

import com.dennis.api_getway.util.JwtUtil;
import io.jsonwebtoken.JwtException;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    private final RouteValidator validator;
    private final JwtUtil jwtUtil;

    public AuthenticationFilter(RouteValidator validator, JwtUtil jwtUtil) {
        super(Config.class);
        this.validator = validator;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            if (validator.isSecured.test(exchange.getRequest())) {

                // Get the Authorization header safely
                String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

                // Check if the Authorization header is null or improperly formatted
                if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                    throw new JwtException("Missing or invalid Authorization header");
                }

                // Extract the token
                String token = authHeader.substring(7);

                try {
                    // Validate the token
                    jwtUtil.validateToken(token);
                    String email = jwtUtil.extractEmail(token);
                    String role = jwtUtil.extractRole(token);

                    ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
                            .header("X-User-Email", email)
                            .header("cross-roads", role)
                            .header("X-TOKEN", token)
                            .build();

                    // Continue the filter chain with the modified request
                    return chain.filter(exchange.mutate().request(modifiedRequest).build());

                } catch (Exception e) {
                    throw new JwtException("UnAuthorised: Invalid token");
                }
            }

            return chain.filter(exchange);
        };
    }

    public static class Config {
    }
}
