package com.userprofileservice.trainee;

import com.userprofileservice.events.CohortResponseDto;
import com.userprofileservice.events.UpdatedCohort;
import com.userprofileservice.exception.ResourceAlreadyExistException;
import com.userprofileservice.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;


import java.io.IOException;
import java.util.List;

public interface TraineesService {


    TraineeResponseDto createTrainee(TraineeDto newTalent) throws ResourceAlreadyExistException, IOException;

    TraineeResponseDto getTraineeDetails(Long userId) throws ResourceNotFoundException;

    TraineeResponseDto updateTraineeDetails(
            TraineeDto updateDto
    ) throws IOException;

    List<CohortResponseDto> getTraineesByCohortId(Long cohortId);
    TraineeResponseDto getTraineeDetailsByEmail(String email);

    boolean updateTraineeStatusByCohort(UpdatedCohort updatedCohort);
    void deleteTrainerDetails(Deactivate deactivate);
    Page<TraineeResponseDto> getAllTrainees(int page, int size);

    Trainee findTraineeDetails(String email);
}