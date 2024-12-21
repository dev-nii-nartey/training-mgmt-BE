package com.userprofileservice.trainee;


import com.userprofileservice.events.*;
import com.userprofileservice.exception.ResourceAlreadyExistException;
import com.userprofileservice.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class TraineesServiceImplTest {

    @Mock
    private TraineesRepository traineesRepository;

    @Mock
    private TraineeValidator validator;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Mock
    private ExternalServiceClient externalServiceClient;

    @InjectMocks
    private TraineesServiceImpl traineesService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createTrainee_Success()  {
        TraineeDto traineeDto = new TraineeDto();
        traineeDto.setEmail("test@example.com");

        when(validator.validateCreate(traineeDto)).thenReturn(null);

        Trainee savedTrainee = new Trainee(traineeDto);
        savedTrainee.setId(1L);
        when(traineesRepository.save(any(Trainee.class))).thenReturn(savedTrainee);

        TraineeResponseDto result = traineesService.createTrainee(traineeDto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(rabbitTemplate).convertAndSend(eq(RabbitMqConfig.SPECIALIZATION_QUEUE), any(SpecializationEvent.class));
    }

//    @Test
//    void createTrainee_AlreadyExists() {
//        TraineeDto traineeDto = new TraineeDto();
//        traineeDto.setEmail("existing@example.com");
//
//        Trainee existingTrainee = new Trainee();
//        existingTrainee.setEmail("existing@example.com");
//        when(validator.validateCreate(traineeDto)).thenReturn(existingTrainee);
//
//        assertThrows(ResourceAlreadyExistException.class, () -> traineesService.createTrainee(traineeDto));
//    }

//    @Test
//    void getTraineeDetails_Success() {
//        Long userId = 1L;
//        Trainee trainee = new Trainee();
//        trainee.setId(userId);
//        when(traineesRepository.findById(userId)).thenReturn(Optional.of(trainee));
//
//        TraineeResponseDto result = traineesService.getTraineeDetails(userId);
//
//        assertNotNull(result);
//        assertEquals(userId, result.getId());
//    }

    @Test
    void getTraineeDetails_NotFound() {
        Long userId = 1L;
        when(traineesRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> traineesService.getTraineeDetails(userId));
    }

    @Test
    void getTraineesByCohortId_Success() {
        Long cohortId = 1L;
        List<Trainee> trainees = Arrays.asList(new Trainee(), new Trainee());
        when(traineesRepository.findAllByCohortId(cohortId)).thenReturn(trainees);

        Map<Long, SpecializationDto> specializationMap = new HashMap<>();
        specializationMap.put(1L, new SpecializationDto(1L, "Specialization 1"));
        when(externalServiceClient.getSpecializationsByIds(anySet())).thenReturn(specializationMap);

        List<CohortResponseDto> result = traineesService.getTraineesByCohortId(cohortId);

        assertNotNull(result);
        assertEquals(2, result.size());
    }


    @Test
    void updateTraineeDetails_NotFound() {
        TraineeDto updateDto = new TraineeDto();
        updateDto.setEmail("nonexistent@example.com");

        when(traineesRepository.findByEmailIgnoreCase(updateDto.getEmail())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> traineesService.updateTraineeDetails(updateDto));
    }

//    @Test
//    void deleteTrainerDetails_Success() {
//        String id = "nonexistent@example.com";
//        Trainee trainee = new Trainee();
//        trainee.setEmail(id);
//        when(traineesRepository.findById(id)).thenReturn(Optional.of(trainee));
//
//        traineesService.deleteTrainerDetails(id);
//
//        verify(traineesRepository).save(trainee);
//        verify(rabbitTemplate).convertAndSend(eq(RabbitMqConfig.SPECIALIZATION_QUEUE), any(SpecializationEvent.class));
//    }
//
//    @Test
//    void deleteTrainerDetails_NotFound() {
//        Long id = 1L;
//        when(traineesRepository.findById(id)).thenReturn(Optional.empty());
//
//        assertThrows(ResourceNotFoundException.class, () -> traineesService.deleteTrainerDetails(id));
//    }
}