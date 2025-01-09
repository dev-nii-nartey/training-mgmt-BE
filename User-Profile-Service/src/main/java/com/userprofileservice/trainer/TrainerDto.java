package com.userprofileservice.trainer;

import com.userprofileservice.events.UserType;
import com.userprofileservice.events.Status;
import jakarta.persistence.Column;
import jakarta.validation.constraints.*;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrainerDto implements Serializable {


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

    private UserType role;

    @NotNull
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

    private String photo;
}