package com.userprofileservice.trainer;


import com.userprofileservice.events.UserType;
import com.userprofileservice.events.Status;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;


@Getter
@Setter
@Entity
@NoArgsConstructor
public class Trainer implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name cannot be blank")
    @Size(min = 3, max = 50, message = "Name must be between 3 and 50 characters")
    private String firstName;

    @NotBlank(message = "Name cannot be blank")
    @Size(min = 3, max = 50, message = "Name must be between 3 and 50 characters")
    private String lastName;

    @Email(message = "Email should be valid")
    @NotBlank(message = "Email is required")
    @Column(unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    private UserType role = UserType.TRAINER;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Status status;


    @NotBlank(message = "Gender is required")
    private String gender;

    @NotBlank(message = "Country is required")
    private String country;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^\\+?[0-9\\-\\s]+$", message = "Invalid phone number format")
    private String phoneNumber;

    @NotNull(message = "Specialization ID is required")
    private Long assignSpecialization;

    @Lob
    private String profilePhoto;


    public Trainer(TrainerDto trainerDto) {
        this.firstName = trainerDto.getFirstName();
        this.lastName = trainerDto.getLastName();
        this.gender = trainerDto.getGender();
        this.phoneNumber = trainerDto.getPhoneNumber();
        this.status = trainerDto.getStatus();
        this.country = trainerDto.getCountry();
        this.assignSpecialization = trainerDto.getAssignSpecialization();
        this.email = trainerDto.getEmail();

        if (trainerDto.getPhoto() != null && !trainerDto.getPhoto().isEmpty()) {
            this.profilePhoto = trainerDto.getPhoto();
        }

    }
}
