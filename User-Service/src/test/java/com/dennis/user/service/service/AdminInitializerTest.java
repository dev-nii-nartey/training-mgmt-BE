package com.dennis.user.service.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.dennis.user.service.model.Admin;
import com.dennis.user.service.model.enums.Role;
import com.dennis.user.service.model.enums.Status;
import com.dennis.user.service.repository.AdminRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

class AdminInitializerTest {

    @InjectMocks
    private AdminInitializer adminInitializer;

    @Mock
    private AdminRepository adminRepository;

    @Mock
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(adminInitializer, "adminEmail", "admin@example.com");
        ReflectionTestUtils.setField(adminInitializer, "adminPassword", "securePassword");
    }

    @Test
    void testRun_CreatesAdminIfNotExist() {
        // Mock repository to return an empty Optional, indicating no existing admin
        when(adminRepository.findByEmail("admin@example.com")).thenReturn(Optional.empty());

        // Execute the run method
        adminInitializer.run();

        // Capture and assert that admin was created with correct properties
        ArgumentCaptor<Admin> adminCaptor = ArgumentCaptor.forClass(Admin.class);
        verify(adminRepository, times(1)).save(adminCaptor.capture());

        Admin savedAdmin = adminCaptor.getValue();
        assertEquals("admin@example.com", savedAdmin.getEmail());
        assertEquals(Role.ADMIN, savedAdmin.getRole());
        assertEquals(Status.ACTIVE, savedAdmin.getStatus());
        assertNotNull(savedAdmin.getCreatedAt());
    }

    @Test
    void testRun_DoesNotCreateAdminIfAlreadyExists() {
        // Mock repository to return a non-empty Optional, indicating admin exists
        when(adminRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(new Admin()));

        // Execute the run method
        adminInitializer.run();

        // Verify that save was not called
        verify(adminRepository, never()).save(any(Admin.class));
    }
}
