package com.dennis.user.service.model;

import com.dennis.user.service.model.enums.Role;
import com.dennis.user.service.model.enums.Status;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "admin")
public class Admin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Email(message = "Email should be valid")
    @NotBlank(message = "Email is required")
    @Column(unique = true)
    private String email;
    @NotNull
    private Role role;
    @NotNull
    private Status status;

    @PastOrPresent(message = "Created time cannot be in the future")
    private LocalDateTime createdAt;

}
