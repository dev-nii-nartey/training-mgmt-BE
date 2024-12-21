package com.specializationservice.events;

import com.specializationservice.services.SpecializationService;

import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;


@Slf4j
@Component
@AllArgsConstructor
public class RabbitMQClient {

    private final SpecializationService specializationService;

    @PostConstruct
    public void init() {
        log.info("RabbitMQClient initialized");
    }


    @RabbitListener(queues = RabbitMqConfig.SPECIALIZATION_QUEUE)
    public void getEntityCount(SpecializationEvent specializationEvent) {
        log.warn("Received specialization event: {}", specializationEvent);
        try {
            if (specializationEvent.getUserType() == UserType.TRAINEE) {
                handleTraineeEvent(specializationEvent);
            } else if (specializationEvent.getUserType() == UserType.TRAINER) {
                handleTrainerEvent(specializationEvent);
            } else {
                throw new IllegalArgumentException("Unknown user type");
            }

            log.info("Specialization event processed successfully: {}", specializationEvent);
        } catch (Exception e) {
            log.trace("Error processing specialization event: {}", e.getMessage());
        }
    }

    private void handleTraineeEvent(SpecializationEvent event) {
        if (event.getEventType() == EnrollmentEventType.ENROLLED) {
            specializationService.enrollTrainee(event.getSpecializationId());
        } else if (event.getEventType() == EnrollmentEventType.UNENROLLED) {
            specializationService.unEnrollTrainee(event.getSpecializationId());
        }
    }

    private void handleTrainerEvent(SpecializationEvent event) {
        if (event.getEventType() == EnrollmentEventType.ENROLLED) {
            specializationService.enrollTrainer(event.getSpecializationId());
        } else if (event.getEventType() == EnrollmentEventType.UNENROLLED) {
            specializationService.unEnrollTrainer(event.getSpecializationId());
        }
    }


}

