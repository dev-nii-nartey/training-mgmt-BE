package com.amponsem.authentication_service.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;


    private String otp;
    private LocalDateTime otpExpiration;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Builder.Default
    private boolean isFirstTime = true;

    public AuthUser(String mail, String password, Role role) {
        this.email = mail;
        this.password = password;
        this.role = role;
    }

}
