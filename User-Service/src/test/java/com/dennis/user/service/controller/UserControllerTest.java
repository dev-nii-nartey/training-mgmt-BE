package com.dennis.user.service.controller;

import com.dennis.user.service.exception.UserNotFoundException;
import com.dennis.user.service.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    void testDeactivateUser() throws Exception {
        String email = "john.doe@example.com";
        when(userService.deactivateUser(email)).thenReturn("User account deactivated successfully.");

        mockMvc.perform(put("/api/v1/users/deactivate")
                        .param("email", email))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User account deactivated successfully."));

        verify(userService, times(1)).deactivateUser(email);
    }

    @Test
    void testDeactivateUserNotFound() throws Exception {
        String email = "notfound@example.com";
        when(userService.deactivateUser(email)).thenThrow(new UserNotFoundException("User not found"));

        mockMvc.perform(put("/api/v1/users/deactivate")
                        .param("email", email))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("User not found"));

        verify(userService, times(1)).deactivateUser(email);
    }
}