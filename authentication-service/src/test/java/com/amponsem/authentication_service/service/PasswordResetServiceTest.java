package com.amponsem.authentication_service.service;

import com.amponsem.authentication_service.model.AuthUser;
import com.amponsem.authentication_service.model.Role;
import com.amponsem.authentication_service.repository.AuthRepository;
import com.amponsem.authentication_service.services.PasswordResetService;
import com.amponsem.authentication_service.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PasswordResetServiceTest {

    @Mock
    private AuthRepository authRepository;

    @Mock
    private UserService userService;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @InjectMocks
    private PasswordResetService passwordResetService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testResetPassword_ValidUser() {
        // Arrange
        String newPassword = "newPassword123";
        String confirmPassword = "newPassword123";
        AuthUser authUser = new AuthUser("test@example.com", "oldPassword", Role.TRAINEE);

        when(userService.authenticatedUser()).thenReturn(authUser);
        when(bCryptPasswordEncoder.encode(newPassword)).thenReturn("encodedPassword");

        // Act
        passwordResetService.resetPassword(newPassword, confirmPassword);

        // Assert
        verify(authRepository, times(1)).save(any(AuthUser.class));
        assertEquals("encodedPassword", authUser.getPassword());
        assertFalse(authUser.isFirstTime());
        assertNull(authUser.getOtp());
    }

    @Test
    void testResetPassword_PasswordMismatch() {
        // Arrange
        String newPassword = "newPassword123";
        String confirmPassword = "differentPassword";
        AuthUser authUser = new AuthUser("test@example.com", "oldPassword", Role.TRAINEE);

        when(userService.authenticatedUser()).thenReturn(authUser);

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () ->
                passwordResetService.resetPassword(newPassword, confirmPassword)
        );
        assertEquals("password does not match", exception.getMessage());
        verify(authRepository, never()).save(any(AuthUser.class));
    }

    @Test
    void testResetPassword_NullUser() {
        // Arrange
        when(userService.authenticatedUser()).thenReturn(null);

        // Act & Assert
        assertThrows(RuntimeException.class, () ->
                passwordResetService.resetPassword("newPassword", "newPassword")
        );
        verify(authRepository, never()).save(any(AuthUser.class));
    }

    @Test
    void testResetPassword_NullPasswords() {
        // Arrange
        AuthUser authUser = new AuthUser("test@example.com", "oldPassword", Role.TRAINEE);
        when(userService.authenticatedUser()).thenReturn(authUser);

        // Act & Assert
        assertThrows(RuntimeException.class, () ->
                passwordResetService.resetPassword(null, "newPassword")
        );
        assertThrows(RuntimeException.class, () ->
                passwordResetService.resetPassword("newPassword", null)
        );
        verify(authRepository, never()).save(any(AuthUser.class));
    }
}
