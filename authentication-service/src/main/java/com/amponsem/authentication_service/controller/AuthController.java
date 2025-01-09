package com.amponsem.authentication_service.controller;

import com.amponsem.authentication_service.dto.AuthResponse;
import com.amponsem.authentication_service.dto.AuthUserDTO;
import com.amponsem.authentication_service.dto.PasswordResetRequest;
import com.amponsem.authentication_service.dto.security.LoginRequest;
import com.amponsem.authentication_service.services.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.ConnectException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final OtpService otpService;
    private final UserService userService;
    private final PasswordResetService passwordResetService;

    
    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody AuthUserDTO authUserDTO) {
        log.info("register request received");
        userService.createAuthUser(authUserDTO);
        return ResponseEntity.ok("User registered in Auth Service");
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        AuthResponse authResponse = authService.authenticate(loginRequest);
        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/send-otp")
    public String requestOtp(@RequestParam String email){

            return otpService.sendOtp(email);
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<Map<String, String>> verifyOtp(@RequestParam String otp) {
        Map<String, String> response = new HashMap<>();
        response.put("token", otpService.confirmOtp(otp));
        return ResponseEntity.ok(response);
    }


    @PutMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody PasswordResetRequest resetRequest) {
        try {
            passwordResetService.resetPassword(
                    resetRequest.newPassword(),
                    resetRequest.confirmPassword()
            );
            return ResponseEntity.ok("Password reset successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(e.getMessage());
        }
    }

    @GetMapping("/test/protected/endpoint")
    public String protectedRoute(){
        return "successfully visited route with token";
    }

    @GetMapping("/test/unprotected/endpoint")
    public String unProtectedRoute(){
        return "successfully visited route with token";
    }
}

