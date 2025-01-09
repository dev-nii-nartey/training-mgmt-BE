// TraineeValidator.java
package com.userprofileservice.trainee;

import com.userprofileservice.events.Status;
import com.userprofileservice.exception.ResourceAlreadyExistException;
import com.userprofileservice.exception.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
public class TraineeValidator {
    private final TraineesRepository traineeRepository;

    public TraineeValidator(TraineesRepository traineeRepository) {
        this.traineeRepository = traineeRepository;
    }

    public Trainee validateCreate(TraineeDto request) {
        Optional<Trainee> existingTrainee = traineeRepository.findByEmailIgnoreCase(request.getEmail());
        if (existingTrainee.isPresent()) {
            Trainee trainee = existingTrainee.get();
            if (trainee.getStatus() == Status.INACTIVE) {
                throw new ResourceAlreadyExistException("Trainee account is INACTIVE. Please contact support."); // Return the inactive trainee for potential reactivation
            } else if (trainee.getStatus() == Status.DEACTIVATED) {
                return trainee;
            } else {
                throw new ResourceAlreadyExistException("Trainee with trainerEmail '" + request.getEmail() + "' already exists");
            }
        }
        return null; // Return null if no existing trainee found
    }

    public void validateUpdate(Trainee trainee, TraineeDto updateData) {
        if (!trainee.getEmail().equalsIgnoreCase(updateData.getEmail())) {
            throw new DataIntegrityViolationException("Email doesn't match");
        }
        if (trainee.getStatus() == Status.DEACTIVATED ) {
            throw new ResourceNotFoundException("Resource you are trying to update doesn't exist");
        }
    }

    public void validateDelete(Trainee trainee) {
        if (trainee.getStatus() == Status.DEACTIVATED ) {
            throw new ResourceNotFoundException("Resource you are trying to delete doesn't exist");
        }
    }
}