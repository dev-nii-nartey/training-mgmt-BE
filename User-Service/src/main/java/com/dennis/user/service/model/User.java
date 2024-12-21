package com.dennis.user.service.model;


import com.dennis.user.service.model.enums.Role;
import com.dennis.user.service.model.enums.Status;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import jakarta.validation.constraints.Email;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "users")
public class User implements Serializable {
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

    @NotNull
    private Role role;
    @NotNull
    private Status status;

    @PastOrPresent(message = "Created time cannot be in the future")
    private LocalDateTime createdAt;

    @PastOrPresent(message = "Updated time cannot be in the future")
    private LocalDateTime updatedAt;
    private Long userId;

}


