package com.userprofileservice.trainer;


import com.userprofileservice.events.RabbitMqConfig;
import com.userprofileservice.events.SpecializationEvent;
import com.userprofileservice.events.Status;
import com.userprofileservice.exception.ResourceAlreadyExistException;
import com.userprofileservice.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class TrainersServiceImplTest {

    @Mock
    private TrainersRepository trainersRepository;

    @Mock
    private TrainerValidator validator;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private TrainersServiceImpl trainersService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createTrainer_Success()  {
        TrainerDto newTrainer = new TrainerDto();
        newTrainer.setEmail("test@example.com");
        Trainer trainer = new Trainer(newTrainer);

        when(validator.validateCreate(any(TrainerDto.class))).thenReturn(null);
        when(trainersRepository.save(any(Trainer.class))).thenReturn(trainer);

        TrainerResponseDto result = trainersService.createTrainer(newTrainer);

        assertNotNull(result);
        verify(trainersRepository).save(any(Trainer.class));
        verify(rabbitTemplate).convertAndSend(eq(RabbitMqConfig.SPECIALIZATION_QUEUE), any(SpecializationEvent.class));
    }

    @Test
    void createTrainer_AlreadyExists() {
        TrainerDto newTrainer = new TrainerDto();
        newTrainer.setEmail("existing@example.com");
        Trainer existingTrainer = new Trainer();
        existingTrainer.setEmail("existing@example.com");

        when(validator.validateCreate(any(TrainerDto.class))).thenReturn(existingTrainer);

        assertThrows(ResourceAlreadyExistException.class, () -> trainersService.createTrainer(newTrainer));
    }

    @Test
    void getTrainerDetails_Success() {
        Long userId = 1L;
        Trainer trainer = new Trainer();
        trainer.setId(userId);

        when(trainersRepository.findById(userId)).thenReturn(Optional.of(trainer));

        TrainerResponseDto result = trainersService.getTrainerDetails(userId);

        assertNotNull(result);
        assertEquals(userId, result.getId());
    }

    @Test
    void getTrainerDetails_NotFound() {
        Long userId = 1L;

        when(trainersRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> trainersService.getTrainerDetails(userId));
    }

    @Test
    void getAllTrainers_Success() {
        int page = 0;
        int size = 10;
        Pageable pageable = PageRequest.of(page, size);
        Page<Trainer> trainerPage = new PageImpl<>(Arrays.asList(new Trainer(), new Trainer()));

        when(trainersRepository.findAllByStatus(eq(Status.ACTIVE), eq(pageable))).thenReturn(trainerPage);

        Page<TrainerResponseDto> result = trainersService.getAllTrainers(page, size);

        assertNotNull(result);
        assertEquals(2, result.getContent().size());
    }

    @Test
    void updateTrainerDetails_Success() throws IOException {
        Long id = 1L;
        TrainerDto updateDto = new TrainerDto();
        updateDto.setFirstName("Updated");
        Trainer existingTrainer = new Trainer();

        when(trainersRepository.findById(id)).thenReturn(Optional.of(existingTrainer));
        when(trainersRepository.save(any(Trainer.class))).thenReturn(existingTrainer);

        TrainerResponseDto result = trainersService.updateTrainerDetails(id, updateDto);

        assertNotNull(result);
        verify(validator).validateUpdate(eq(existingTrainer), eq(updateDto));
        verify(trainersRepository).save(existingTrainer);
    }

    @Test
    void deleteTrainerDetails_Success() {
        Long id = 1L;
        Trainer trainer = new Trainer();
        trainer.setAssignSpecialization(1L);

        when(trainersRepository.findById(id)).thenReturn(Optional.of(trainer));

        trainersService.deleteTrainerDetails(id);

        verify(validator).validateDelete(trainer);
        verify(trainersRepository).save(trainer);
        verify(rabbitTemplate).convertAndSend(eq(RabbitMqConfig.SPECIALIZATION_QUEUE), any(SpecializationEvent.class));
        assertEquals(Status.DEACTIVATED, trainer.getStatus());
    }
}