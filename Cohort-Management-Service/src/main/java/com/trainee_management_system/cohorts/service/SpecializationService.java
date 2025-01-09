package com.trainee_management_system.cohorts.service;

import com.trainee_management_system.cohorts.resources.Specialization;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.List;

@Service
@Slf4j
public class SpecializationService {
    @Value("${services.specialization.base-url}")
    private String specializationBaseUrl;

    private final WebClient.Builder webClientBuilder;

    public SpecializationService(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    @CircuitBreaker(name = "ExternalServiceBreaker", fallbackMethod = "fallbackSpecializations")
    public List<Specialization> getSpecializationsByIds(List<Long> specializationIds) {
        return Flux.fromIterable(specializationIds)
                .flatMap(id -> webClientBuilder
                        .build()
                        .get()
                        .uri(specializationBaseUrl + "/specializations/" + id)
                        .retrieve()
                        .bodyToMono(Specialization.class)
                        .timeout(Duration.ofSeconds(4))
                        .doOnError(e -> log.error("Error fetching specialization with ID {}: {}", id, e.getMessage()))
                )
                .collectList()
                .block();
    }

    private List<Specialization> fallbackSpecializations(List<Long> specializationIds, Throwable throwable) {
        log.error("Fallback triggered: {}", throwable.getMessage());
        return specializationIds.stream()
                .map(id -> new Specialization(id, "Specialization Unavailable", "Service not reachable"))
                .toList();
    }
}