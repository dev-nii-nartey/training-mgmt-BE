package com.userprofileservice.trainer;

import com.userprofileservice.events.Status;
import com.userprofileservice.exception.ResourceAlreadyExistException;
import com.userprofileservice.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrainerValidatorTest {

    @Mock
    private TrainersRepository trainersRepository;

    private TrainerValidator trainerValidator;

    @BeforeEach
    void setUp() {
        trainerValidator = new TrainerValidator(trainersRepository);
    }

    @Test
    void validateCreate_WhenTrainerDoesNotExist_ReturnsNull() {
        // Arrange
        TrainerDto request = new TrainerDto();
        request.setEmail("new@example.com");
        when(trainersRepository.findByEmailIgnoreCase(anyString())).thenReturn(Optional.empty());

        // Act
        Trainer result = trainerValidator.validateCreate(request);

        // Assert
        assertNull(result);
    }

    @Test
    void validateCreate_WhenTrainerExistsAndActive_ThrowsException() {
        // Arrange
        TrainerDto request = new TrainerDto();
        request.setEmail("existing@example.com");

        Trainer existingTrainer = new Trainer();
        existingTrainer.setEmail("existing@example.com");
        existingTrainer.setStatus(Status.ACTIVE);

        when(trainersRepository.findByEmailIgnoreCase(anyString()))
                .thenReturn(Optional.of(existingTrainer));

        // Act & Assert
        assertThrows(ResourceAlreadyExistException.class,
                () -> trainerValidator.validateCreate(request));
    }

    @Test
    void validateCreate_WhenTrainerExistsAndInactive_ReturnsTrainer() {
        // Arrange
        TrainerDto request = new TrainerDto();
        request.setEmail("inactive@example.com");

        Trainer inactiveTrainer = new Trainer();
        inactiveTrainer.setEmail("inactive@example.com");
        inactiveTrainer.setStatus(Status.INACTIVE);

        when(trainersRepository.findByEmailIgnoreCase(anyString()))
                .thenReturn(Optional.of(inactiveTrainer));

        // Act
        Trainer result = trainerValidator.validateCreate(request);

        // Assert
        assertNotNull(result);
        assertEquals(Status.INACTIVE, result.getStatus());
        assertEquals("inactive@example.com", result.getEmail());
    }

    @Test
    void validateUpdate_WhenEmailsMatch_AndStatusActive_Success() {
        // Arrange
        Trainer trainer = new Trainer();
        trainer.setEmail("test@example.com");
        trainer.setStatus(Status.ACTIVE);

        TrainerDto updateDto = new TrainerDto();
        updateDto.setEmail("test@example.com");

        // Act & Assert
        assertDoesNotThrow(() -> trainerValidator.validateUpdate(trainer, updateDto));
    }

    @Test
    void validateUpdate_WhenEmailsDontMatch_ThrowsException() {
        // Arrange
        Trainer trainer = new Trainer();
        trainer.setEmail("test@example.com");
        trainer.setStatus(Status.ACTIVE);

        TrainerDto updateDto = new TrainerDto();
        updateDto.setEmail("different@example.com");

        // Act & Assert
        assertThrows(DataIntegrityViolationException.class,
                () -> trainerValidator.validateUpdate(trainer, updateDto));
    }

    @Test
    void validateUpdate_WhenTrainerDeactivated_ThrowsException() {
        // Arrange
        Trainer trainer = new Trainer();
        trainer.setEmail("test@example.com");
        trainer.setStatus(Status.DEACTIVATED);

        TrainerDto updateDto = new TrainerDto();
        updateDto.setEmail("test@example.com");

        // Act & Assert
        assertThrows(ResourceNotFoundException.class,
                () -> trainerValidator.validateUpdate(trainer, updateDto));
    }

    @Test
    void validateDelete_WhenTrainerActive_Success() {
        // Arrange
        Trainer trainer = new Trainer();
        trainer.setStatus(Status.ACTIVE);

        // Act & Assert
        assertDoesNotThrow(() -> trainerValidator.validateDelete(trainer));
    }

    @Test
    void validateDelete_WhenTrainerDeactivated_ThrowsException() {
        // Arrange
        Trainer trainer = new Trainer();
        trainer.setStatus(Status.DEACTIVATED);

        // Act & Assert
        assertThrows(ResourceNotFoundException.class,
                () -> trainerValidator.validateDelete(trainer));
    }
}