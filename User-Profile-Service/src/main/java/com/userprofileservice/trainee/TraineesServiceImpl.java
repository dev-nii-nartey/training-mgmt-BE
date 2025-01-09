package com.userprofileservice.trainee;


import com.userprofileservice.events.*;
import com.userprofileservice.exception.ResourceNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
@Transactional
public class TraineesServiceImpl implements TraineesService {
    private final TraineesRepository traineesRepository;
    private final TraineeValidator validator;
    private final RabbitTemplate rabbitTemplate;
    private final ExternalServiceClient externalServiceClient;


    @Override
    public TraineeResponseDto createTrainee(TraineeDto traineeDto)  {
        Trainee existingTrainee = validator.validateCreate(traineeDto);

        if (existingTrainee != null) {
            updateTraineeDetails(traineeDto);
        }

        Trainee trainee = new Trainee(traineeDto);
        Trainee saved = traineesRepository.save(trainee);

        rabbitTemplate.convertAndSend(RabbitMqConfig.SPECIALIZATION_QUEUE,
                new SpecializationEvent(trainee.getSpecialization(), EnrollmentEventType.ENROLLED, UserType.TRAINEE));

        return new TraineeResponseDto(saved);
    }

    @Transactional(readOnly = true)
    @Override
    public TraineeResponseDto getTraineeDetails(Long userId) {
        Trainee trainee = traineesRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Trainee not found for user: " + userId));

        return getTraineeResponseDto(trainee);
    }

    @Override
    @Transactional(readOnly = true)
    public Trainee findTraineeDetails(String email) {

        return traineesRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResourceNotFoundException("Trainee not found for user: " + email));
    }

    @Transactional(readOnly = true)
    public TraineeResponseDto fetchTraineeDetails(String email) {

        Trainee trainee = traineesRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResourceNotFoundException("Trainee not found for user: " + email));

        return getTraineeResponseDto(trainee);
    }

    private TraineeResponseDto getTraineeResponseDto(Trainee trainee) {
        Set<Long> specId = Set.of(trainee.getSpecialization());
        Set<Long> cohortIds = Set.of(trainee.getCohortId());

        List<CohortDto> cohortsMono = Mono
                .fromCallable(() -> externalServiceClient.getCohortsByIds(cohortIds)).block();

        Map<Long, SpecializationDto> specializationMap = externalServiceClient.getSpecializationsByIds(specId);

        return mapToResponseDto(trainee, specializationMap, cohortsMono);
    }


    @Transactional(readOnly = true)
    @Override
    public TraineeResponseDto getTraineeDetailsByEmail(String email) {
        Trainee trainee = traineesRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResourceNotFoundException("Trainee not found for user: " + email));
        return new TraineeResponseDto(trainee);
    }


    @Transactional(readOnly = true)
    @Override
    public List<CohortResponseDto> getTraineesByCohortId(Long cohortId) {
        List<Trainee> trainees = traineesRepository.findAllByCohortId(cohortId);

        Set<Long> specializationIds = trainees.stream()
                .map(Trainee::getSpecialization)
                .collect(Collectors.toSet());

        Map<Long, SpecializationDto> specializationMap = externalServiceClient.getSpecializationsByIds(specializationIds);

        return trainees.stream()
                .map(trainee -> {
                    CohortResponseDto dto = new CohortResponseDto(trainee);
                    SpecializationDto spec = specializationMap.get(trainee.getSpecialization());
                    dto.setSpecializationName(spec != null ? spec.getName() : "Unknown");
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public Page<TraineeResponseDto> getAllTrainees(int page, int size) {
        Page<Trainee> traineesPage = traineesRepository.findAllByStatusNot(Status.DEACTIVATED, PageRequest.of(page, size));
        Set<Long> specializationIds = traineesPage.getContent().stream()
                .map(Trainee::getSpecialization)
                .collect(Collectors.toSet());

        Set<Long> cohortIds = traineesPage.getContent().stream()
                .map(Trainee::getCohortId)
                .collect(Collectors.toSet());

        List<CohortDto> cohortsMono = Mono
                .fromCallable(() -> externalServiceClient.getCohortsByIds(cohortIds)).block();

        Map<Long, SpecializationDto> specializationMap = externalServiceClient.getSpecializationsByIds(specializationIds);


        return traineesPage.map(trainee -> mapToResponseDto(trainee, specializationMap, cohortsMono));
    }

    private TraineeResponseDto mapToResponseDto(Trainee trainee, Map<Long, SpecializationDto> specializationMap, List<CohortDto> cohortsMono) {
        TraineeResponseDto dto = new TraineeResponseDto(trainee);
        SpecializationDto spec = specializationMap.get(trainee.getSpecialization());
        CohortDto cohort = cohortsMono.stream()
                .filter(c -> c.id().equals(trainee.getCohortId()))
                .findFirst()
                .orElse(null);
        dto.setSpecialization(spec != null ? spec.getName() : null);
        dto.setCohort(cohort != null ? cohort.name() : null);
        return dto;
    }

    @Override
    @Transactional
    public TraineeResponseDto updateTraineeDetails(TraineeDto updateDto)  {
        Trainee trainee = traineesRepository.findByEmailIgnoreCase(updateDto.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Trainee not found with trainerEmail: " + updateDto.getEmail()));

        validator.validateUpdate(trainee, updateDto);

        updateTraineeFields(trainee, updateDto);

        trainee.setStatus(updateDto.getStatus() == Status.DEACTIVATED ? Status.DEACTIVATED : trainee.getStatus());

        Trainee save = traineesRepository.save(trainee);
        return new TraineeResponseDto(save);
    }

    private void updateTraineeFields(Trainee trainee, TraineeDto updateDto) {
        trainee.setFirstName(updateDto.getFirstName());
        trainee.setLastName(updateDto.getLastName());
        trainee.setEmail(updateDto.getEmail());
        trainee.setSpecialization(updateDto.getSpecialization());
        trainee.setCohortId(updateDto.getCohortId());
        trainee.setPhoneNumber(updateDto.getPhoneNumber());
        trainee.setCountry(updateDto.getCountry());
        trainee.setAddress(updateDto.getAddress());
        trainee.setDateOfBirth(updateDto.getDateOfBirth());
        trainee.setGender(updateDto.getGender());
        trainee.setUniversityCompleted(updateDto.getUniversityCompleted());
        trainee.setEnrollmentDate(updateDto.getEnrollmentDate());
//        trainee.setRole(updateDto.getRole());
        trainee.setStatus(updateDto.getStatus());
        // Save the photo bytes if a file was uploaded
        if (updateDto.getPhoto() != null && !updateDto.getPhoto().isEmpty()) {
            trainee.setProfilePhoto(updateDto.getPhoto());
        }
    }


    @Transactional
    public boolean updateTraineeStatusByCohort(UpdatedCohort updatedCohort) {
        try {
            List<Trainee> trainees = traineesRepository.findAllByCohortId(updatedCohort.getCohortId());
            for (Trainee trainee : trainees) {
                trainee.setStatus(updatedCohort.getStatus());
            }
            traineesRepository.saveAll(trainees);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void deleteTrainerDetails(Deactivate deactivate) {
        Trainee trainee = traineesRepository.findByEmailIgnoreCase(deactivate.email())
                .orElseThrow(() -> new ResourceNotFoundException("Trainer not found : "));
        validator.validateDelete(trainee);
        trainee.setStatus(Status.DEACTIVATED);
        traineesRepository.save(trainee);
        rabbitTemplate.convertAndSend(RabbitMqConfig.SPECIALIZATION_QUEUE, new SpecializationEvent(trainee.getSpecialization(), EnrollmentEventType.UNENROLLED, UserType.TRAINEE));
    }


}