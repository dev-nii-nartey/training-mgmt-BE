package com.amponsem.authentication_service.service;

import com.amponsem.authentication_service.dto.AuthResponse;
import com.amponsem.authentication_service.model.Role;
import com.amponsem.authentication_service.dto.security.LoginRequest;
import com.amponsem.authentication_service.model.AuthUser;
import com.amponsem.authentication_service.repository.AuthRepository;
import com.amponsem.authentication_service.services.AuthService;
import com.amponsem.authentication_service.services.CustomUserDetails;
import com.amponsem.authentication_service.services.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthRepository authRepository;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAuthenticate_ValidCredentials_NotFirstTime() {
        // Arrange
        String email = "test@example.com";
        String password = "password";
        LoginRequest request = new LoginRequest(email, password);

        // Mock AuthUser
        AuthUser authUser = AuthUser.builder()
                .email(email)
                .password(password)
                .role(Role.ADMIN)
                .isFirstTime(false)
                .build();

        // Mock Authentication
        Authentication authentication = mock(Authentication.class);
        CustomUserDetails userDetails = new CustomUserDetails(email, password, Role.ADMIN);

        // Setup mocks
        when(authRepository.findByEmail(email)).thenReturn(Optional.of(authUser));
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(jwtService.generateToken(email, Role.ADMIN.name())).thenReturn("valid_jwt_token");

        // Act
        AuthResponse response = authService.authenticate(request);

        // Assert
        assertNotNull(response);
        assertEquals("valid_jwt_token", response.getToken());
        assertFalse(response.isFirstTime());
        verify(authRepository, times(1)).findByEmail(email);
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtService, times(1)).generateToken(email, Role.ADMIN.name());
    }

    @Test
    void testAuthenticate_ValidCredentials_FirstTime() {
        // Arrange
        String email = "test@example.com";
        String password = "password";
        LoginRequest request = new LoginRequest(email, password);

        // Mock AuthUser with firstTime true
        AuthUser authUser = AuthUser.builder()
                .email(email)
                .password(password)
                .role(Role.ADMIN)
                .isFirstTime(true)
                .build();

        // Mock Authentication
        Authentication authentication = mock(Authentication.class);
        CustomUserDetails userDetails = new CustomUserDetails(email, password, Role.ADMIN);

        // Setup mocks
        when(authRepository.findByEmail(email)).thenReturn(Optional.of(authUser));
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(jwtService.generateToken(email, Role.ADMIN.name())).thenReturn("valid_jwt_token");

        // Act
        AuthResponse response = authService.authenticate(request);

        // Assert
        assertNotNull(response);
        assertEquals("valid_jwt_token", response.getToken());
        assertTrue(response.isFirstTime());
        verify(authRepository, times(1)).findByEmail(email);
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtService, times(1)).generateToken(email, Role.ADMIN.name());
    }

    @Test
    void testAuthenticate_InvalidCredentials() {
        // Arrange
        String email = "test@example.com";
        String password = "wrong_password";
        LoginRequest request = new LoginRequest(email, password);

        // Mock AuthUser
        AuthUser authUser = AuthUser.builder()
                .email(email)
                .password(password)
                .role(Role.ADMIN)
                .build();

        // Setup mocks
        when(authRepository.findByEmail(email)).thenReturn(Optional.of(authUser));
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Invalid credentials provided."));

        // Act & Assert
        BadCredentialsException exception = assertThrows(BadCredentialsException.class, () ->
                authService.authenticate(request)
        );

        assertEquals("Invalid credentials provided.", exception.getMessage());
        verify(authRepository, times(1)).findByEmail(email);
        verify(jwtService, never()).generateToken(anyString(), anyString());
    }

    @Test
    void testAuthenticate_UserNotFound() {
        // Arrange
        String email = "nonexistent@example.com";
        String password = "password";
        LoginRequest request = new LoginRequest(email, password);

        // Setup mock
        when(authRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Act & Assert
        BadCredentialsException exception = assertThrows(BadCredentialsException.class, () ->
                authService.authenticate(request)
        );

        assertEquals("Invalid email or password", exception.getMessage());
        verify(authRepository, times(1)).findByEmail(email);
        verify(authenticationManager, never()).authenticate(any());
        verify(jwtService, never()).generateToken(anyString(), anyString());
    }
}