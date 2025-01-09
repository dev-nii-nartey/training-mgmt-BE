package com.userprofileservice.trainee;


import com.userprofileservice.events.Status;
import com.userprofileservice.events.UserType;
import jakarta.persistence.Column;

import jakarta.validation.constraints.*;
import lombok.*;


import java.io.Serializable;
import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TraineeDto implements Serializable {

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

    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;

    @NotEmpty
    @NotBlank(message = "Gender is required")
    private String gender;

    @NotBlank(message = "Country is required")
    private String country;

    @NotBlank(message = "Address is required")
    private String address;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^\\+?[0-9\\-\\s]+$", message = "Invalid phone number format")
    private String phoneNumber;

    @NotNull
    @NotBlank(message = "University completed is required")
    private String universityCompleted;

    private String Photo;

    @NotNull(message = "Specialization ID is required")
    private Long specialization;

    @NotNull(message = "Cohort ID is required")
    private Long cohortId;

    @PastOrPresent(message = "Enrollment date must be in the past or present")
    private LocalDate enrollmentDate;

    private Long trainingId;

}




