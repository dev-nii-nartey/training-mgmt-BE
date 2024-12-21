package com.amponsem.authentication_service.services;

import com.amponsem.authentication_service.dto.AuthResponse;
import com.amponsem.authentication_service.model.AuthUser;
import com.amponsem.authentication_service.repository.AuthRepository;
import com.amponsem.authentication_service.dto.security.LoginRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class  AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final AuthRepository authRepository;

    public AuthResponse authenticate(LoginRequest request) {
        log.info("logging user in");
        AuthUser authUser = authRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));
        log.info("user email: {}", authUser.getEmail());
        try {
            // Authenticate the user with email and password
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
            log.info("password: {}", request.getPassword());


            // Retrieve authenticated user details
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            String token = jwtService.generateToken(userDetails.getUsername(), userDetails.getRole());
          if (authUser.isFirstTime()) {
                return AuthResponse.builder()
                        .token(token)
                        .isFirstTime(true)
                        .build();
            }
            return AuthResponse.builder()
                    .token(token)
                    .build();
        } catch (BadCredentialsException e) {
            log.error("Authentication failed for user: {}", request.getEmail());
            throw new BadCredentialsException("Invalid credentials provided.");
        }
    }
}
