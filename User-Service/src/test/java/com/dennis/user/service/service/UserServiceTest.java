package com.dennis.user.service.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


import com.dennis.user.service.config.RabbitConfig;
import com.dennis.user.service.dto.UserDTO;
import com.dennis.user.service.exception.EmailAlreadyExistsException;
import com.dennis.user.service.exception.UserNotFoundException;
import com.dennis.user.service.model.Email;
import com.dennis.user.service.model.User;
import com.dennis.user.service.model.enums.Status;
import com.dennis.user.service.model.enums.Role;
import com.dennis.user.service.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import java.time.LocalDateTime;
import java.util.*;


class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Mock
    private AuthService authService;

    @Mock
    private PasswordService passwordService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateUser_Success() {
        String email = "test@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(passwordService.passwordGenerator()).thenReturn("password123");

        userService.createUser("John", "Doe", email, Status.ACTIVE, Role.TRAINEE);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository, times(1)).save(userCaptor.capture());
        verify(rabbitTemplate, times(1))
                .convertAndSend(eq(RabbitConfig.EMAIL_QUEUE), any(Email.class));

        User savedUser = userCaptor.getValue();
        assertEquals("John", savedUser.getFirstName());
        assertEquals("Doe", savedUser.getLastName());
        assertEquals(email, savedUser.getEmail());
        assertEquals(Status.ACTIVE, savedUser.getStatus());
    }

    @Test
    void testCreateUser_EmailAlreadyExists() {
        String email = "test@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(new User()));

        assertThrows(EmailAlreadyExistsException.class, () ->
                userService.createUser("John", "Doe", email, Status.ACTIVE, Role.TRAINEE));

        verify(userRepository, never()).save(any(User.class));
        verify(rabbitTemplate, never()).convertAndSend(anyString(), any(Email.class));
    }

    @Test
    void testGetAllUsers() {
        List<User> users = Arrays.asList(
                new User(1L,"John", "Doe", "john@example.com", Role.TRAINEE, Status.ACTIVE, LocalDateTime.now(), LocalDateTime.now()),
                new User(2L,"Jane", "Smith", "jane@example.com", Role.TRAINEE, Status.ACTIVE, LocalDateTime.now(), null)
        );
        when(userRepository.findAll()).thenReturn(users);

        List<UserDTO> userDTOs = userService.getAllUsers();

        assertEquals(2, userDTOs.size());
        assertEquals("John", userDTOs.get(0).getFirstName());
        assertEquals("Jane", userDTOs.get(1).getFirstName());
    }

    @Test
    void testDeactivateUser_Success() {
        String email = "test@example.com";
        User user = new User(1L,"John", "Doe", email, Role.TRAINEE, Status.ACTIVE, LocalDateTime.now(), LocalDateTime.now());
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        String result = userService.deactivateUser(email);

        assertEquals("User account deactivated successfully.", result);
        assertEquals(Status.DEACTIVATED, user.getStatus());
    }

    @Test
    void testDeactivateUser_UserNotFound() {
        String email = "nonexistent@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.deactivateUser(email));
    }

    @Test
    void testActivateUser_AlreadyActive() {
        String email = "test@example.com";
        User user = new User(1L,"John", "Doe", email, Role.TRAINEE, Status.ACTIVE, LocalDateTime.now(), LocalDateTime.now());
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        String result = userService.activateUser(email);

        assertEquals("User account is already active.", result);
    }
}
