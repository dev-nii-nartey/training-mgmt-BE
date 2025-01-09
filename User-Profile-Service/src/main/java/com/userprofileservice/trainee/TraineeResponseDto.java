package com.userprofileservice.trainee;

import com.userprofileservice.events.Status;
import com.userprofileservice.events.UserType;


import java.time.LocalDate;
import java.time.LocalDateTime;


import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TraineeResponseDto {
        private Long id;
        private String firstName;
        private String lastName;
        private String email;
        private UserType role;
        private Status status;
        private LocalDate dateOfBirth;
        private String gender;
        private String country;
        private String address;
        private String phoneNumber;
        private String universityCompleted;
        private String profilePhoto;
        private String specialization;
        private String cohort;
        private LocalDate enrollmentDate;
        private Long trainingId;
        private LocalDateTime dateAdded;

        public TraineeResponseDto(Trainee trainee) {
                this.id = trainee.getId();
                this.firstName = trainee.getFirstName();
                this.lastName = trainee.getLastName();
                this.dateOfBirth = trainee.getDateOfBirth();
                this.enrollmentDate = trainee.getEnrollmentDate();
                this.address = trainee.getAddress();
                this.universityCompleted = trainee.getUniversityCompleted();
                this.gender = trainee.getGender();
                this.specialization = String.valueOf(trainee.getSpecialization());
                this.cohort = String.valueOf(trainee.getCohortId());
                this.email = trainee.getEmail();
                this.phoneNumber = trainee.getPhoneNumber();
                this.trainingId = trainee.getTrainingId();
                this.country = trainee.getCountry();
                this.status = trainee.getStatus();
                this.role = trainee.getRole();
                this.dateAdded = trainee.getDateCreated();

                if (trainee.getProfilePhoto() != null) {
                        this.profilePhoto = trainee.getProfilePhoto();

                }
        }
}
