package com.userprofileservice.events;


import com.userprofileservice.exception.ResourceAlreadyExistException;
import com.userprofileservice.trainee.TraineeDto;
import com.userprofileservice.trainee.TraineeResponseDto;
import com.userprofileservice.trainee.TraineesService;
import com.userprofileservice.trainer.TrainerDto;
import com.userprofileservice.trainer.TrainerResponseDto;
import com.userprofileservice.trainer.TrainersService;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Component
@AllArgsConstructor
public class RabbitMQClient {

    private final TrainersService trainersService;
    private final RabbitTemplate rabbitTemplate;
    private final TraineesService traineesService;

    @PostConstruct
    public void init() {
        log.info("RabbitMQClient initialized");
    }

    @RabbitListener(queues = RabbitMqConfig.TRAINER_PROFILE_QUEUE)
    public void getTrainerProfile(TrainerDto trainerDetails) {
        log.warn("Received trainer message: {}", trainerDetails);
        try {
            TrainerResponseDto trainerDto = trainersService.createTrainer(trainerDetails);
            log.info("Successfully created trainer: {}", trainerDto);
        } catch (ResourceAlreadyExistException e) {
            log.warn("trainer already exists: {}", e.getMessage());
        } catch (Exception e) {
            log.error("Error processing trainer registration: {}", e.getMessage());

        }
    }


    @RabbitListener(queues = RabbitMqConfig.TRAINEE_PROFILE_QUEUE)
    public void getTraineeProfile(TraineeDto traineeDto) {
        log.warn("Received trainee message: {}", traineeDto);

        try {
            TraineeResponseDto trainee = traineesService.createTrainee(traineeDto);
            log.info(" Trainee created successfully: {}", trainee);
        } catch (ResourceAlreadyExistException e) {
            log.warn("Trainee already exists: {}", e.getMessage());
        } catch (Exception e) {
            log.error("Error processing trainee registration: {}", e.getMessage());
        }
    }


    @RabbitListener(queues = RabbitMqConfig.COHORT_QUEUE)
    public void getCohortStatus(UpdatedCohort updatedCohort) {
        log.warn("Received cohort status message: {}", updatedCohort);
        try {
            boolean trainee = traineesService.updateTraineeStatusByCohort(updatedCohort);
//            log.info(" Trainee status updated successfully: {}", trainee);
        } catch (ResourceAlreadyExistException e) {
            log.warn("Error updating trainee status {}", e.getMessage());
        } catch (Exception e) {
            log.error("Error processing trainee status: {}", e.getMessage());
        }
    }


    public void pushMessageToSpecializationServer() {
        String messageString = " Specialization @" + LocalDateTime.now().format(DateTimeFormatter.ISO_TIME);
        rabbitTemplate.convertAndSend("hello", messageString);
    }
}

