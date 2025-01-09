package com.userprofileservice.trainee;


import com.userprofileservice.events.Status;
import com.userprofileservice.events.UserType;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Setter
@Entity
public class Trainee implements Serializable {

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
    private UserType role = UserType.TRAINEE;

    @NotNull
    @Enumerated(EnumType.STRING)
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

    @Lob
    private String profilePhoto;

    @NotNull(message = "Specialization ID is required")
    private Long specialization;

    @NotNull(message = "Cohort ID is required")
    private Long cohortId;

    @PastOrPresent(message = "Enrollment date must be in the past or present")
    private LocalDate enrollmentDate;

    @NotNull(message = "Training ID is required")
    private Long trainingId;

    private LocalDateTime dateCreated = LocalDateTime.now();

    public Trainee(TraineeDto traineeDto)  {
        this.firstName = traineeDto.getFirstName();
        this.lastName = traineeDto.getLastName();
        this.gender = traineeDto.getGender();
        this.phoneNumber = traineeDto.getPhoneNumber();
        this.status = traineeDto.getStatus();
        this.country = traineeDto.getCountry();
        this.specialization = traineeDto.getSpecialization();
        this.email = traineeDto.getEmail();
        this.cohortId = traineeDto.getCohortId();
        this.universityCompleted = traineeDto.getUniversityCompleted();
        this.address = traineeDto.getAddress();
        this.dateOfBirth = traineeDto.getDateOfBirth();
        this.enrollmentDate = traineeDto.getEnrollmentDate();
        this.trainingId = traineeDto.getTrainingId();

        if (traineeDto.getPhoto() != null && !traineeDto.getPhoto().isEmpty()) {
            this.profilePhoto = traineeDto.getPhoto();
        }
    }

}
