package com.dennis.user.service.model;

import com.dennis.user.service.model.enums.Role;
import com.dennis.user.service.model.enums.Status;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.*;
import jakarta.validation.constraints.Email;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;
import java.time.LocalDate;

@Data
public class Trainee implements Serializable {

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

    private Role role;
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

    private MultipartFile profilePhoto;

    @NotNull(message = "Specialization ID is required")
    private Long specialization;

    @NotNull(message = "Cohort ID is required")
    private Long cohortId;

    @PastOrPresent(message = "Enrollment date must be in the past or present")
    private LocalDate enrollmentDate;

    private Long trainingId;
}
