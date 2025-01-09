package com.userprofileservice.trainer;


import com.userprofileservice.events.Status;
import com.userprofileservice.exception.ResourceAlreadyExistException;
import com.userprofileservice.exception.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
public class TrainerValidator {
    private final TrainersRepository trainersRepository;

    public TrainerValidator(TrainersRepository trainersRepository) {
        this.trainersRepository = trainersRepository;

    }

    public Trainer validateCreate(TrainerDto request) {
        Optional<Trainer> existingTrainer = trainersRepository.findByEmailIgnoreCase(request.getEmail());
        if (existingTrainer.isPresent()) {
            Trainer trainer = existingTrainer.get();
            if (trainer.getStatus() == Status.INACTIVE) {
                return trainer; // Return the inactive trainer for reactivation
            } else {
                throw new ResourceAlreadyExistException("Trainer with traineeEmail '" + request.getEmail() + "' already exists");
            }
        }
        return null; // Return null if no existing trainer found
    }

    public void validateUpdate(Trainer trainer, TrainerDto updateDto) {
        if (!trainer.getEmail().equalsIgnoreCase(updateDto.getEmail())) {
            throw new DataIntegrityViolationException("Email doesn't match");
        }
        if (trainer.getStatus() == Status.DEACTIVATED ) {
            throw new ResourceNotFoundException("Resource you are trying to update doesn't exist");
        }
    }


    public void validateDelete(Trainer trainer) {
        if (trainer.getStatus() == Status.DEACTIVATED) {
            throw new ResourceNotFoundException("Resource you are trying to delete doesn't exist");
        }
    }
}
