package com.dennis.user.service.service;

import com.dennis.user.service.config.RabbitConfig;
import com.dennis.user.service.dto.ProfileUpdateDTO;
import com.dennis.user.service.dto.TraineeDTO;
import com.dennis.user.service.dto.TrainerDTO;
import com.dennis.user.service.model.Trainee;
import com.dennis.user.service.model.Trainer;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.time.Duration;

@Service
public class ProfileService {
    private final RabbitTemplate rabbitTemplate;
    private static final Logger log = LoggerFactory.getLogger(ProfileService.class);
    private final WebClient.Builder webClientBuilder;

    public ProfileService(RabbitTemplate rabbitTemplate, WebClient.Builder webClientBuilder) {
        this.rabbitTemplate = rabbitTemplate;
        this.webClientBuilder = webClientBuilder;
    }

    public void sendTraineeProfile(Trainee trainee){
        String base64Photo = convertMultipartPhoto(trainee.getProfilePhoto());
        TraineeDTO traineeDTO = new TraineeDTO(trainee, base64Photo);
        rabbitTemplate.convertAndSend(RabbitConfig.TRAINEE_PROFILE_QUEUE, traineeDTO);
    }

    public void sendTrainerProfile(Trainer trainer){
        String base64Photo = convertMultipartPhoto(trainer.getProfilePhoto());
        TrainerDTO trainerDTO = new TrainerDTO(trainer, base64Photo);
        rabbitTemplate.convertAndSend(RabbitConfig.TRAINER_PROFILE_QUEUE, trainerDTO);
    }

    public String convertMultipartPhoto(MultipartFile profilePhoto){
        String base64Photo = "";
        try {
            if (profilePhoto != null){
                base64Photo = FileUtils.convertMultipartFileToBase64(profilePhoto);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return base64Photo;
    }

    @CircuitBreaker(name = "profileService", fallbackMethod = "fallbackForProfileService")
    @RateLimiter(name = "profileServiceRateLimiter", fallbackMethod = "rateLimiterFallback")
    public void sendProfileUpdate(ProfileUpdateDTO profileUpdateDTO) {
        try {
            String response = webClientBuilder.build()
                    .post()
                    .uri("lb://USER-PROFILE-SERVICE/api/v1/profiles/trainees/status")
                    .body(Mono.just(profileUpdateDTO), ProfileUpdateDTO.class)
                    .retrieve()
                    .onStatus(
                            status -> status.is4xxClientError() || status.is5xxServerError(),
                            clientResponse -> clientResponse.bodyToMono(String.class)
                                    .flatMap(errorMessage -> Mono.error(new RuntimeException("Error: " + errorMessage)))
                    )
                    .bodyToMono(String.class)
                    .timeout(Duration.ofSeconds(4))
                    .block();
        } catch (WebClientResponseException e) {
            System.err.println("HTTP Status Code: " + e.getStatusCode());
            System.err.println("Error Response Body: " + e.getResponseBodyAsString());
        } catch (RuntimeException e) {
            System.err.println("Error: " + e.getMessage());
        }

    }

    public void fallbackForProfileService(ProfileUpdateDTO profileUpdateDTO, Throwable ex) {
        log.error("Fallback triggered for send to profile Service: {}", ex.getMessage());
        //TODO: add alternative logic(ie sending auth dto to a message broker when Auth service can't be reached normally
    }


    public void rateLimiterFallback(ProfileUpdateDTO profileUpdateDTO, Throwable ex) {
        log.warn("Rate limiter fallback triggered for send to profile Service: {}", ex.getMessage());
    }

}