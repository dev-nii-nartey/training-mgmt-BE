package com.userprofileservice.trainee;


import com.userprofileservice.events.Status;
import com.userprofileservice.exception.ResourceAlreadyExistException;
import com.userprofileservice.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TraineeValidatorTest {

    private TraineesRepository traineeRepository;
    private TraineeValidator traineeValidator;

    @BeforeEach
    void setUp() {
        traineeRepository = mock(TraineesRepository.class);
        traineeValidator = new TraineeValidator(traineeRepository);
    }

//    @Test
//    void validateCreate_whenTraineeExistsAndIsInactive_shouldReturnTrainee() {
//        TraineeDto request = new TraineeDto();
//        request.setEmail("test@example.com");
//
//        Trainee existingTrainee = new Trainee();
//        existingTrainee.setStatus(Status.INACTIVE);
//
//        when(traineeRepository.findByEmailIgnoreCase(request.getEmail())).thenReturn(Optional.of(existingTrainee));
//
//        Trainee result = traineeValidator.validateCreate(request);
//
//        assertEquals(existingTrainee, result);
//    }
//
//    @Test
//    void validateCreate_whenTraineeExistsAndIsDeactivated_shouldThrowException() {
//        TraineeDto request = new TraineeDto();
//        request.setEmail("test@example.com");
//
//        Trainee existingTrainee = new Trainee();
//        existingTrainee.setStatus(Status.DEACTIVATED);
//
//        when(traineeRepository.findByEmailIgnoreCase(request.getEmail())).thenReturn(Optional.of(existingTrainee));
//
//        assertThrows(ResourceAlreadyExistException.class, () -> traineeValidator.validateCreate(request));
//    }

    @Test
    void validateCreate_whenTraineeExistsAndIsActive_shouldThrowException() {
        TraineeDto request = new TraineeDto();
        request.setEmail("test@example.com");

        Trainee existingTrainee = new Trainee();
        existingTrainee.setStatus(Status.ACTIVE);

        when(traineeRepository.findByEmailIgnoreCase(request.getEmail())).thenReturn(Optional.of(existingTrainee));

        assertThrows(ResourceAlreadyExistException.class, () -> traineeValidator.validateCreate(request));
    }

    @Test
    void validateCreate_whenTraineeDoesNotExist_shouldReturnNull() {
        TraineeDto request = new TraineeDto();
        request.setEmail("test@example.com");

        when(traineeRepository.findByEmailIgnoreCase(request.getEmail())).thenReturn(Optional.empty());

        Trainee result = traineeValidator.validateCreate(request);

        assertNull(result);
    }

    @Test
    void validateUpdate_whenEmailDoesNotMatch_shouldThrowException() {
        Trainee trainee = new Trainee();
        trainee.setEmail("test@example.com");

        TraineeDto updateData = new TraineeDto();
        updateData.setEmail("different@example.com");

        assertThrows(DataIntegrityViolationException.class, () -> traineeValidator.validateUpdate(trainee, updateData));
    }

    @Test
    void validateUpdate_whenTraineeIsDeactivated_shouldThrowException() {
        Trainee trainee = new Trainee();
        trainee.setEmail("test@example.com");
        trainee.setStatus(Status.DEACTIVATED);

        TraineeDto updateData = new TraineeDto();
        updateData.setEmail("test@example.com");

        assertThrows(ResourceNotFoundException.class, () -> traineeValidator.validateUpdate(trainee, updateData));
    }

    @Test
    void validateDelete_whenTraineeIsDeactivated_shouldThrowException() {
        Trainee trainee = new Trainee();
        trainee.setStatus(Status.DEACTIVATED);

        assertThrows(ResourceNotFoundException.class, () -> traineeValidator.validateDelete(trainee));
    }
}