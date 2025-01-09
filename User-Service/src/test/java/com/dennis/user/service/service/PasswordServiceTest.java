package com.dennis.user.service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCrypt;

import static org.junit.jupiter.api.Assertions.*;


class PasswordServiceTest {

    @InjectMocks
    private PasswordService passwordService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testPasswordGenerator_Length() {
        // Generate a password
        String password = passwordService.passwordGenerator();

        // Assert password is at least 8 characters long
        assertNotNull(password);
        assertEquals(8, password.length());
    }

    @Test
    void testPasswordGenerator_ContainsValidCharacters() {
        String password = passwordService.passwordGenerator();

        // Define valid characters
        String validCharacters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()-_=+";

        // Check all characters in the generated password are within valid characters
        for (char ch : password.toCharArray()) {
            assertTrue(validCharacters.indexOf(ch) >= 0, "Invalid character found: " + ch);
        }
    }

    @Test
    void testHashPassword_NotNullOrEmpty() {
        String plainPassword = "Test@123";

        // Hash password
        String hashedPassword = passwordService.hashPassword(plainPassword);

        // Assert the hashed password is not null or empty
        assertNotNull(hashedPassword);
        assertFalse(hashedPassword.isEmpty());
    }

    @Test
    void testHashPassword_IsBCryptFormat() {
        String plainPassword = "Test@123";

        // Hash password
        String hashedPassword = passwordService.hashPassword(plainPassword);

        // Assert the hashed password starts with $2a$ (BCrypt hash indicator)
        assertTrue(hashedPassword.startsWith("$2a$"));
    }

    @Test
    void testHashPassword_MatchesOriginal() {
        String plainPassword = "Test@123";

        // Hash password
        String hashedPassword = passwordService.hashPassword(plainPassword);

        // Verify the password matches the hash
        assertTrue(BCrypt.checkpw(plainPassword, hashedPassword));
    }

    @Test
    void testHashPassword_FailsForIncorrectPassword() {
        String plainPassword = "Test@123";
        String wrongPassword = "Wrong@456";

        // Hash password
        String hashedPassword = passwordService.hashPassword(plainPassword);

        // Verify wrong password does not match the hash
        assertFalse(BCrypt.checkpw(wrongPassword, hashedPassword));
    }
}
