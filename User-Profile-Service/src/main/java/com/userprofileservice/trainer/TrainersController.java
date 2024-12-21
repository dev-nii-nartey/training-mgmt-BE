package com.userprofileservice.trainer;



import com.userprofileservice.events.Finder;
import com.userprofileservice.trainee.TraineeResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;



@Slf4j
@RestController
@RequestMapping("/api/v1/profiles/trainers")
@RequiredArgsConstructor
public class TrainersController {

    private final TrainersServiceImpl trainerProfileService;


    @PostMapping
    public ResponseEntity<TrainerResponseDto> createTrainer(@Valid @ModelAttribute TrainerDto trainerDetails) {
        TrainerResponseDto trainer = trainerProfileService.createTrainer(trainerDetails);
        return ResponseEntity.status(HttpStatus.CREATED).body(trainer);
    }


    @GetMapping
    public Page<TrainerResponseDto> getAllTrainers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size
    ) {
        return trainerProfileService.getAllTrainers(page, size);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TrainerResponseDto> getTrainer(@PathVariable String id) {
        TrainerResponseDto response;

        try {
            Long numericId = (Long) Long.parseLong(id);
            response = trainerProfileService.getTrainerDetails(numericId);
        } catch (NumberFormatException e) {
            response = trainerProfileService.getTrainerByEmail(id);
        }

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateTrainerDetails(
            @PathVariable("id") Long id,
            @ModelAttribute @Valid TrainerDto updateDto
    ) {
        try {

            TrainerResponseDto  updatedDetails = trainerProfileService.updateTrainerDetails(id,
                    updateDto);
            return ResponseEntity.ok(updatedDetails);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body("Error processing files.");
        }
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTrainer(@PathVariable Long id) {
        trainerProfileService.deleteTrainerDetails(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/fetch/{email}")
    public TrainerResponseDto findTrainerByEmail(@PathVariable String email) {
        return trainerProfileService.findTrainerDetails(email);
    }

    @PostMapping("/find")
    public ResponseEntity<Trainer> findTrainee(@RequestBody Finder find) {
        return ResponseEntity.ok(trainerProfileService.fetchTraineeDetails(find.email()));
    }

}