package com.amponsem.authentication_service.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public record PasswordResetRequest(

        @NotEmpty(message = "Password cannot be empty")
        @Size(min = 8, message = "Password must be at least 8 characters long")
        String newPassword,

        @NotEmpty(message = "Password cannot be empty")
        @Size(min = 8, message = "Password must be at least 8 characters long")
        String confirmPassword
){

    public PasswordResetRequest {
        if (newPassword == null || newPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("new password is required");
        }
        if (confirmPassword == null || confirmPassword.trim().isEmpty()) {
            throw new IllegalArgumentException(" confirm Password is required");
        }
    }
}