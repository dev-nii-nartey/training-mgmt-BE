package com.dennis.user.service.service;

import com.dennis.user.service.dto.AuthUserDTO;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Service
@Slf4j
public class AuthService {
    private final WebClient.Builder webClientBuilder;

    @Value("${auth.service.url}")
    private String authServiceUrl;



    public AuthService(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    @CircuitBreaker(name = "authService", fallbackMethod = "fallbackForAuthService")
    @RateLimiter(name = "authServiceRateLimiter", fallbackMethod = "rateLimiterFallback")
    public void sendToAuthService(AuthUserDTO authUserDTO) {
        log.warn("Webclient in action");

        String response = webClientBuilder.build()
                .post()
                .uri("lb://AUTHENTICATION-SERVICE/api/v1/auth/register")
                .body(Mono.just(authUserDTO), AuthUserDTO.class)
                .retrieve()
                .onStatus(
                        status -> status.is4xxClientError() || status.is5xxServerError(),
                        clientResponse -> clientResponse.bodyToMono(String.class)
                                .flatMap(errorMessage -> Mono.error(new RuntimeException("Error: " + errorMessage)))
                )
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(4))
                .block();


        log.info("Response: {}", response);


    }

    @CircuitBreaker(name = "authService", fallbackMethod = "fallbackForAuthService")
    @RateLimiter(name = "authServiceRateLimiter", fallbackMethod = "rateLimiterFallback")
    public void sendAdminToAuthService(AuthUserDTO authUserDTO) {
        log.warn("Webclient in action");

        String response = webClientBuilder.build()
                .post()
                .uri("lb://AUTHENTICATION-SERVICE/admin/add")
                .body(Mono.just(authUserDTO), AuthUserDTO.class)
                .retrieve()
                .onStatus(
                        status -> status.is4xxClientError() || status.is5xxServerError(),
                        clientResponse -> clientResponse.bodyToMono(String.class)
                                .flatMap(errorMessage -> Mono.error(new RuntimeException("Error: " + errorMessage)))
                )
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(4))
                .block();


        log.info("Response: {}", response);


    }


    public void fallbackForAuthService(AuthUserDTO authUserDTO, Throwable ex) {
        log.error("Fallback triggered for sendToAuthService: {}", ex.getMessage());
        log.error("Executing fallback method");
        //TODO: add alternative logic(ie sending auth dto to a message broker) when Auth service can't be reached normally
    }

    public void rateLimiterFallback(AuthUserDTO authUserDTO, Throwable ex) {
        log.error("Rate limiter fallback triggered for sendToAuthService: {}", ex.getMessage());
    }
}