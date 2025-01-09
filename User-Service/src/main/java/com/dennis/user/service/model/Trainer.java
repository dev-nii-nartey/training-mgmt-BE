package com.dennis.user.service.model;

import com.dennis.user.service.model.enums.Role;
import com.dennis.user.service.model.enums.Status;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;


@Data
public class Trainer implements Serializable {

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


    @NotBlank(message = "Gender is required")
    private String gender;

    @NotBlank(message = "Country is required")
    private String country;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^\\+?[0-9\\-\\s]+$", message = "Invalid phone number format")
    private String phoneNumber;

    @NotNull(message = "Specialization ID is required")
    private Long assignSpecialization;

    private MultipartFile profilePhoto;
}
