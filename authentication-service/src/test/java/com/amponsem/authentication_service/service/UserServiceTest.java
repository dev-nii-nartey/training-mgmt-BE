package com.amponsem.authentication_service.service;

import com.amponsem.authentication_service.dto.AuthUserDTO;
import com.amponsem.authentication_service.model.Role;
import com.amponsem.authentication_service.repository.AuthRepository;
import com.amponsem.authentication_service.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private AuthRepository authRepository;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateAuthUser() {
        AuthUserDTO authUserDTO = new AuthUserDTO("newuser@example.com", "password123", Role.TRAINEE);

        // Act
        userService.createAuthUser(authUserDTO);

        // Assert
        verify(authRepository, times(1)).save(argThat(authUser ->
                authUser.getEmail().equals("newuser@example.com") &&
                        authUser.getPassword().equals("password123") &&
                        authUser.getRole() == Role.TRAINEE
        ));
    }
}

