package com.amponsem.authentication_service.dto;

import com.amponsem.authentication_service.model.Role;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AuthUserDTO {
    @Email(message = "Email should be valid")
    @NotBlank(message = "Email is required")
    @Column(unique = true)
    private String email;

    @NotNull
    private String password;

    @NotNull
    private Role role;

    public AuthUserDTO(String mail, String password123, Role role) {
        this.email = mail;
        this.password = password123;
        this.role = role;
    }
}