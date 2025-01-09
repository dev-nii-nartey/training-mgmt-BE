package com.dennis.user.service.service;

import com.dennis.user.service.dto.AuthUserDTO;
import com.dennis.user.service.model.Admin;
import com.dennis.user.service.model.enums.Role;
import com.dennis.user.service.model.enums.Status;
import com.dennis.user.service.repository.AdminRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class AdminInitializer implements CommandLineRunner {


    private final AdminRepository adminRepository;
    private final PasswordService passwordService;
    private final AuthService authService;

    @Value("${admin.user.email}")
    private String adminEmail;

    @Value("${admin.user.password}")
    private String adminPassword;

    public AdminInitializer(AdminRepository adminRepository, PasswordService passwordService, AuthService authService) {
        this.adminRepository = adminRepository;
        this.passwordService = passwordService;
        this.authService = authService;
    }

    @Override
    public void run(String... args) {
        if (adminRepository.findByEmail(adminEmail).isEmpty()) {
            Admin adminUser = Admin.builder()
                    .email(adminEmail)
                    .role(Role.ADMIN)
                    .status(Status.ACTIVE)
                    .createdAt(LocalDateTime.now())
                    .build();

            adminRepository.save(adminUser);

            AuthUserDTO authUserDTO = AuthUserDTO.builder()
                    .email(adminEmail)
                    .password(passwordService.hashPassword(adminPassword))
                    .isFirstTime(true)
                    .role(Role.ADMIN)
                    .build();
            authService.sendAdminToAuthService(authUserDTO);
        }
    }
}