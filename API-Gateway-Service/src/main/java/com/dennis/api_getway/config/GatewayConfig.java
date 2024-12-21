package com.dennis.api_getway.config;

import org.apache.http.HttpHeaders;
import org.springframework.cloud.gateway.config.GlobalCorsProperties;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
public class GatewayConfig {

    
    @Bean
    public CorsWebFilter corsWebFilter(GlobalCorsProperties globalCorsProperties) {
        CorsConfiguration corsConfig = new CorsConfiguration();
        corsConfig.setAllowedOrigins(Arrays.asList(
                "http://localhost:4200",
                "https://training-sys-kahoot-private.vercel.app",
                "https://training-management.amalitech-dev.net",
                "https://dev.duu3qoluscyig.amplifyapp.com",
                "https://endlessly-alert-gnu.ngrok-free.app"
        ));
        corsConfig.addAllowedMethod("*");
        corsConfig.addAllowedHeader("*");
        corsConfig.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);

        return new CorsWebFilter(source);
    }

    @Bean
    public RouteLocator myRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route(predicateSpec -> predicateSpec
                        .path("**")
                        .uri("lb://LIVE-QUIZ-SERVICE")
                )
                .build();
    }
}
