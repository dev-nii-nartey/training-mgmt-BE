package com.dennis.api_getway.filter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.function.Predicate;

@Component
public class RouteValidator {

    // Properly annotated with @Value
    @Value("${opened-endpoints}")
    private String[] openApiEndpoints;

    // Predicate to determine if a request is secured
    public Predicate<ServerHttpRequest> isSecured =
            request -> {
                return Arrays.stream(openApiEndpoints)
                        .noneMatch(uri -> { 
                            return request.getURI().getPath().startsWith(uri);
                        });
            };
}
