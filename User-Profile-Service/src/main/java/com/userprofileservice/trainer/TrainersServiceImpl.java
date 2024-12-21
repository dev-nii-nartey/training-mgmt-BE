package com.userprofileservice.trainer;



import com.userprofileservice.events.*;
import com.userprofileservice.exception.ResourceAlreadyExistException;
import com.userprofileservice.exception.ResourceNotFoundException;

import com.userprofileservice.trainee.Trainee;
import com.userprofileservice.trainee.TraineeResponseDto;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;


import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;


@Service
@Slf4j
@AllArgsConstructor
@Transactional
public class TrainersServiceImpl implements TrainersService {
    private final TrainersRepository trainersRepository;
    private final TrainerValidator validator;
    private final RabbitTemplate rabbitTemplate;
    private final ExternalServiceClient externalServiceClient;


    @Override
    public TrainerResponseDto createTrainer(TrainerDto newTrainer) {
        Trainer existingTrainer = validator.validateCreate(newTrainer);

        if (existingTrainer != null) {
            throw new ResourceAlreadyExistException("Trainer with traineeEmail " + existingTrainer.getEmail() + " already exists");
        }
        Trainer trainer = new Trainer(newTrainer);
        Trainer save = trainersRepository.save(trainer);
        rabbitTemplate.convertAndSend(RabbitMqConfig.SPECIALIZATION_QUEUE,new SpecializationEvent(trainer.getAssignSpecialization(), EnrollmentEventType.ENROLLED, UserType.TRAINER));
        return new TrainerResponseDto(save);
    }




    @Transactional(readOnly = true)
    @Override
    public TrainerResponseDto getTrainerDetails(Long userId) {
        Trainer trainer = trainersRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Trainer not found for user: " + userId));
        return new TrainerResponseDto(trainer);
    }



    @Override
    @Transactional(readOnly = true)
    public Page<TrainerResponseDto> getAllTrainers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return trainersRepository.findAllByStatus(Status.ACTIVE, pageable)
                .map(TrainerResponseDto::new);
    }



    @Override
    public TrainerResponseDto updateTrainerDetails(Long id, TrainerDto updateDto) throws IOException {
        Trainer trainer = trainersRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Trainer not found : "));
        validator.validateUpdate(trainer,updateDto);
        updateTrainerFields(trainer, updateDto);
        trainer.setStatus(updateDto.getStatus() == Status.DEACTIVATED ? Status.DEACTIVATED : trainer.getStatus());
        Trainer save = trainersRepository.save(trainer);
        return new TrainerResponseDto(save);
    }

    private void updateTrainerFields(Trainer trainer, TrainerDto updateDto) {
        trainer.setFirstName(updateDto.getFirstName());
        trainer.setLastName(updateDto.getLastName());
        trainer.setAssignSpecialization(updateDto.getAssignSpecialization());
        trainer.setPhoneNumber(updateDto.getPhoneNumber());
        trainer.setCountry(updateDto.getCountry());
        trainer.setGender(updateDto.getGender());

        // Only set role if it's not null
        if (updateDto.getRole() != null) {
            trainer.setRole(updateDto.getRole());
        }

        trainer.setStatus(updateDto.getStatus());

        // Save the photo bytes if a file was uploaded
        if (updateDto.getPhoto() != null && !updateDto.getPhoto().isEmpty()) {
            trainer.setProfilePhoto(updateDto.getPhoto());
        }
    }



    @Override
    public void deleteTrainerDetails(Long id) {
        Trainer trainer = trainersRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Trainer not found : "));
        validator.validateDelete(trainer);
        trainer.setStatus(Status.DEACTIVATED);
        trainersRepository.save(trainer);
        rabbitTemplate.convertAndSend(RabbitMqConfig.SPECIALIZATION_QUEUE,new SpecializationEvent(trainer.getAssignSpecialization(), EnrollmentEventType.UNENROLLED, UserType.TRAINER));
    }

    @Override
    public Trainer fetchTraineeDetails(String email) {
        Trainer trainer = trainersRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResourceNotFoundException("Trainee not found for user: " + email));
        return trainer;
    }

    @Override
    public TrainerResponseDto findTrainerDetails(String email) {
        Trainer trainer = trainersRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResourceNotFoundException("Trainee not found for user: " + email));
        return getTrainerResponseDto(trainer);
    }

    private TrainerResponseDto getTrainerResponseDto(Trainer trainer) {
        Set<Long> specId = Set.of(trainer.getAssignSpecialization());

        Map<Long, SpecializationDto> specializationMap = externalServiceClient.getSpecializationsByIds(specId);

        return mapToResponseDto(trainer, specializationMap);
    }



    private TrainerResponseDto mapToResponseDto(Trainer trainer, Map<Long, SpecializationDto> specializationMap) {
        TrainerResponseDto dto = new TrainerResponseDto(trainer);
        SpecializationDto spec = specializationMap.get(trainer.getAssignSpecialization());
        dto.setAssignSpecialization(spec != null ? spec.getName() : null);
        return dto;
    }

    public TrainerResponseDto getTrainerByEmail(String email) {
        Trainer trainer = trainersRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResourceNotFoundException("Trainer not found for user: " + email));
        return new TrainerResponseDto(trainer);

    }
}