package com.userprofileservice.events;


import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Slf4j
public class ExternalServiceClient {



    @Value("${specializationUrl}")
    private String SPECIALIZATION_SERVICE_URL;

    @Value("${cohortUrl}")
    private String COHORT_SERVICE_URL;

    private final WebClient.Builder webClient;

    public ExternalServiceClient(WebClient.Builder webClient) {
        this.webClient = webClient;
    }


    @CircuitBreaker(name = "specializationFallback", fallbackMethod = "fallbackSpecialization")
    @RateLimiter(name = "SpecializationRateLimiter", fallbackMethod = "fallbackRateLimiter")
    public Map<Long, SpecializationDto> getSpecializationsByIds(Set<Long> specializationIds) {
        return webClient.build()
                .post() // Changed to POST for batch retrieval
                .uri(SPECIALIZATION_SERVICE_URL + "/batch")
                .bodyValue(new SpecializationBatchRequest(specializationIds))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<Long, SpecializationDto>>() {})
                .block();
    }


    public Map<Long, SpecializationDto> fallbackSpecialization(Set<Long> specializationIds, Throwable throwable) {
        log.error("Specialization Fallback triggered: {}", throwable.getMessage());
        return specializationIds.stream()
                .collect(Collectors.toMap(
                        id -> id,
                        id -> SpecializationDto.builder()
                                .id(id)
                                .name("Specialization Unavailable")
                                .build()
                ));
    }

    public Map<Long, SpecializationDto> fallbackRateLimiter(Set<Long> specializationIds, RequestNotPermitted ex) {
        log.error("Specialization Rate-limiter Fallback triggered: {}", ex.getMessage());
        return specializationIds.stream()
                .collect(Collectors.toMap(
                        id -> id,
                        id -> SpecializationDto.builder()
                                .id(id)
                                .name("Rate Limited")
                                .build()
                ));
    }



    @CircuitBreaker(name = "ExternalServiceBreaker", fallbackMethod = "fallbackCohorts")
    public List<CohortDto> getCohortsByIds(Set<Long> cohortIds){
        return Flux.fromIterable(cohortIds)
                .flatMap(id -> webClient
                        .build()
                        .get()
                        .uri(COHORT_SERVICE_URL  + id)
                        .retrieve()
                        .bodyToMono(CohortDto.class)
                        .timeout(Duration.ofSeconds(4))
                        .doOnError(e -> log.error("Error fetching cohorts with ID {}: {}", id, e.getMessage()))
                )
                .collectList()
                .block();
    }

    private List<CohortDto> fallbackCohorts(Set<Long> cohortIds, Throwable throwable) {
        log.error("Cohort Fallback triggered: {}", throwable.getMessage());
        return cohortIds.stream()
                .map(id -> new CohortDto(id, "Cohort Unavailable"))
                .collect(Collectors.toList()); }
}



/*

         =======================ASYNCHRONOUS WAY TO MAKE A POST REQUEST USING WEBCLIENT =====================

        Mono<Void> responseMono = webClient
            .post()
            .uri(TALENT_SERVICE_URL + "/earn-badge")
            .body(Mono.just(assignBadgeDto), AssignBadgeDto.class)
            .retrieve()
            .bodyToMono(Void.class);

        responseMono.subscribe(
        success -> log.info("Badge assignment completed successfully"),
        error -> log.error("Error during badge assignment", error)
);



            =====================SYNCHRONOUS WAY TO MAKER A WEBCLIENT POST REQUEST USING WEBCLIENT=================
                webClient
                        .build()
                        .post()
                        .uri(TALENT_SERVICE_URL + "/earn-badge")
                        .body(Mono.just(assignBadgeDto), AssignBadgeDto.class)
                        .retrieve()
                        .bodyToMono(Void.class)
                        .block();  // Blocks until the response is received

                log.info("Badge assignment completed successfully");
 Using RestTemplate for simplicity, in a real-world scenario, use WebClient for better performance and handling of retries, timeouts, etc.
*/
