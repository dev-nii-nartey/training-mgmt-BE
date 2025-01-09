package com.userprofileservice.trainer;

import com.userprofileservice.exception.ResourceAlreadyExistException;
import com.userprofileservice.exception.ResourceNotFoundException;
import com.userprofileservice.trainee.TraineeResponseDto;
import org.springframework.data.domain.Page;

import java.io.IOException;


public interface TrainersService {
    TrainerResponseDto createTrainer(TrainerDto newTalent) throws ResourceAlreadyExistException, IOException;
    TrainerResponseDto getTrainerDetails(Long userId) throws ResourceNotFoundException;

    Page<TrainerResponseDto> getAllTrainers(int page, int pageSize);

    TrainerResponseDto updateTrainerDetails(Long id,
       TrainerDto trainerDto
    ) throws IOException;

    TrainerResponseDto findTrainerDetails(String email);

    void deleteTrainerDetails(Long userId);

    Trainer fetchTraineeDetails(String email);
}
