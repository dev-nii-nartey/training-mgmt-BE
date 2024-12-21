package com.amponsem.authentication_service.service;

import com.amponsem.authentication_service.config.RabbitMQConfig;
import com.amponsem.authentication_service.exception.InvalidOTPException;
import com.amponsem.authentication_service.model.AuthUser;
import com.amponsem.authentication_service.model.Email;
import com.amponsem.authentication_service.model.Role;
import com.amponsem.authentication_service.repository.AuthRepository;
import com.amponsem.authentication_service.services.JwtService;
import com.amponsem.authentication_service.services.OtpService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class OtpServiceTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Mock
    private AuthRepository authRepository;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private OtpService otpService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSendOtp_ValidUser() {
        // Arrange
        AuthUser authUser = new AuthUser("test@example.com", "password", Role.TRAINEE);
        when(authRepository.findByEmail("test@example.com")).thenReturn(Optional.of(authUser));

        // Act
        String otp = otpService.sendOtp("test@example.com");

        // Assert
        assertNotNull(otp);
        verify(rabbitTemplate, times(1)).convertAndSend(eq(RabbitMQConfig.OTP_EMAIL_QUEUE), any(Email.class));
        verify(authRepository, times(1)).save(any(AuthUser.class));
    }

    @Test
    void testConfirmOtp_ValidOtp() {
        // Arrange
        String email = "test@example.com";
        String otp = "123456";
        String expectedToken = "dummy-jwt-token";

        AuthUser authUser = new AuthUser(email, "password", Role.TRAINEE);
        authUser.setOtp(otp);

        when(authRepository.findByOtp(otp)).thenReturn(Optional.of(authUser));
        when(jwtService.generateToken(email, null)).thenReturn(expectedToken);

        // Act
        String result = otpService.confirmOtp(otp);

        // Assert
        assertEquals(expectedToken, result);
        verify(jwtService, times(1)).generateToken(email, null);
    }

    @Test
    void testConfirmOtp_InvalidOtp() {
        // Arrange
        when(authRepository.findByOtp("654321")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(InvalidOTPException.class, () -> otpService.confirmOtp("654321"));
    }

    @Test
    void testConfirmOtp_OtpMismatch() {
        // Arrange
        String storedOtp = "123456";
        String providedOtp = "654321";
        AuthUser authUser = new AuthUser("test@example.com", "password", Role.TRAINEE);
        authUser.setOtp(storedOtp);

        when(authRepository.findByOtp(providedOtp)).thenReturn(Optional.of(authUser));

        // Act
        String result = otpService.confirmOtp(providedOtp);

        // Assert
        assertEquals("Invalid OTP", result);
        verify(jwtService, never()).generateToken(any(), any());
    }
}
