package com.userprofileservice.trainer;

import com.userprofileservice.events.UserType;
import com.userprofileservice.events.Status;

import lombok.*;

import java.io.Serializable;



@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrainerResponseDto implements Serializable {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private UserType role;
    private Status status;
    private String gender;
    private String country;
    private String phoneNumber;
    private String assignSpecialization;
    private String profilePhoto;


    public TrainerResponseDto(Trainer trainer) {
        this.id = trainer.getId();
        this.firstName = trainer.getFirstName();
        this.lastName = trainer.getLastName();
        this.gender = trainer.getGender();
        this.email = trainer.getEmail();
        this.phoneNumber = trainer.getPhoneNumber();
        this.country = trainer.getCountry();
        this.status = trainer.getStatus();
        this.role = trainer.getRole();

        // Convert byte[] to base64 string
        if (trainer.getProfilePhoto() != null) {
            this.profilePhoto = trainer.getProfilePhoto();

        }

    }
}









