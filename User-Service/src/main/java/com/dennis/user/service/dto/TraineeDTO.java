package com.dennis.user.service.dto;

import com.dennis.user.service.model.Trainee;
import lombok.Data;

@Data
public class TraineeDTO extends Trainee {
    private String photo;

    public TraineeDTO(Trainee trainee, String base64Photo) {
        super();
        this.setFirstName(trainee.getFirstName());
        this.setLastName(trainee.getLastName());
        this.setEmail(trainee.getEmail());
        this.setRole(trainee.getRole());
        this.setStatus(trainee.getStatus());
        this.setDateOfBirth(trainee.getDateOfBirth());
        this.setGender(trainee.getGender());
        this.setCountry(trainee.getCountry());
        this.setAddress(trainee.getAddress());
        this.setPhoneNumber(trainee.getPhoneNumber());
        this.setUniversityCompleted(trainee.getUniversityCompleted());
        this.setSpecialization(trainee.getSpecialization());
        this.setCohortId(trainee.getCohortId());
        this.setEnrollmentDate(trainee.getEnrollmentDate());
        this.setTrainingId(trainee.getTrainingId());

        // Set the converted profile photo
        this.photo = base64Photo;
    }
}