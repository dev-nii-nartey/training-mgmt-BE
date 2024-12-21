package com.amponsem.authentication_service.dto.security;

import lombok.Data;

@Data
public class LoginRequest {
    private String email;
    private String password;

    public LoginRequest(String mail, String password) {
        this.email = mail;
        this.password = password;
    }
}
