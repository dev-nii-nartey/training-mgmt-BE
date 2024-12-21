package com.userprofileservice.trainee;



import com.userprofileservice.events.CohortResponseDto;
import com.userprofileservice.events.Finder;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Slf4j
@RestController
@RequestMapping("/api/v1/profiles/trainees")
@RequiredArgsConstructor
public class TraineesController {

    private final TraineesServiceImpl traineeProfileService;

    @PostMapping
    public ResponseEntity<TraineeResponseDto> createTrainee(@Valid @ModelAttribute TraineeDto traineeDetails)  {
        TraineeResponseDto savedTraineeDto = traineeProfileService.createTrainee(traineeDetails);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(savedTraineeDto);
    }


    @GetMapping("/{id}")
    public ResponseEntity<TraineeResponseDto> getTrainee(@PathVariable("id") Long userId) {
        return ResponseEntity.ok(traineeProfileService.getTraineeDetails(userId));
    }

    @PostMapping("/find")
    public ResponseEntity<Trainee> findTrainee(@RequestBody Finder find) {
        return ResponseEntity.ok(traineeProfileService.findTraineeDetails(find.email()));
    }

    @GetMapping("/cohort/{id}")
    public List<CohortResponseDto> getTraineesByCohortId(@PathVariable("id") Long cohortId) {
        return traineeProfileService.getTraineesByCohortId(cohortId);
    }


    @GetMapping
    public Page<TraineeResponseDto> getAllTrainees(   @RequestParam(defaultValue = "0") int page,
                                                      @RequestParam(defaultValue = "50") int size
    ) {
        return traineeProfileService.getAllTrainees(page,size);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateTraineeDetails(
            @ModelAttribute TraineeDto updateDto
    )  {
        TraineeResponseDto updatedDetails = traineeProfileService.updateTraineeDetails(updateDto);
        return ResponseEntity.ok(updatedDetails);
    }

    @PostMapping("/status")
    public ResponseEntity<Void> deleteTrainee(@RequestBody Deactivate delete) {
        traineeProfileService.deleteTrainerDetails(delete);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/fetch/{email}")
    public TraineeResponseDto findTraineeByEmail(@PathVariable String email) {
        return traineeProfileService.fetchTraineeDetails(email);
    }



}