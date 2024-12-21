package com.amponsem.authentication_service.services;

import com.amponsem.authentication_service.model.AuthUser;
import com.amponsem.authentication_service.repository.AuthRepository;
import com.amponsem.authentication_service.exception.PasswordResetException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * Service responsible for handling password reset operations.
 * Implements security best practices and comprehensive validation.
 */
@Slf4j
@Service
public class PasswordResetService {

    private final AuthRepository authRepository;
    private final UserService userService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public PasswordResetService(AuthRepository authRepository,
                                UserService userService,
                                BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.authRepository = authRepository;
        this.userService = userService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    /**
     * Resets user password with validation checks.
     *
     * @param newPassword     The new password to set
     * @param confirmPassword Confirmation of the new password
     * @throws PasswordResetException if validation fails or operation cannot be completed
     */
    @Transactional
    public void resetPassword(String newPassword, String confirmPassword) {
        try {
            // Validate input parameters
            validatePasswordInputs(newPassword, confirmPassword);

            // Get and validate authenticated user
            AuthUser authUser = getAuthenticatedUser();

            // Verify password matching and reuse
            validatePasswordChange(newPassword, authUser);

            // Update user password and related fields
            updateUserPassword(authUser, newPassword);

        } catch (Exception e) {
            handlePasswordResetError(e);
            throw new PasswordResetException("Password reset failed: ", e);
        }
    }

    /**
     * Validates password inputs for null and emptiness.
     */
    private void validatePasswordInputs(String newPassword, String confirmPassword) {
        if (!StringUtils.hasText(newPassword) || !StringUtils.hasText(confirmPassword)) {
            throw new PasswordResetException("Password fields cannot be empty");
        }

        if (!newPassword.equals(confirmPassword)) {
            throw new PasswordResetException("Passwords do not match");
        }

        validatePasswordComplexity(newPassword);
    }

    /**
     * Validates password complexity requirements.
     */
    private void validatePasswordComplexity(String password) {
        if (password.length() < 8) {
            throw new PasswordResetException("Password must be at least 8 characters long");
        }

        if (!password.matches(".*[A-Z].*")) {
            throw new PasswordResetException("Password must contain at least one capital letter");
        }
    }

    /**
     * Retrieves and validates the authenticated user.
     */
    private AuthUser getAuthenticatedUser() {
        AuthUser authUser = userService.authenticatedUser();
        if (authUser == null) {
            throw new PasswordResetException("No authenticated user found");
        }
        return authUser;
    }

    /**
     * Validates the password change, including reuse check.
     */
    private void validatePasswordChange(String newPassword, AuthUser authUser) {
        if (bCryptPasswordEncoder.matches(newPassword, authUser.getPassword())) {
            throw new PasswordResetException("New password cannot be the same as current password");
        }
    }

    /**
     * Updates the user's password and related fields.
     */
    private void updateUserPassword(AuthUser authUser, String newPassword) {
        try {
            authUser.setPassword(bCryptPasswordEncoder.encode(newPassword));
            authUser.setFirstTime(false);
            authUser.setOtp(null);
            authUser.setOtpExpiration(null);

            authRepository.save(authUser);
            log.debug("User password and related fields updated successfully");

        } catch (Exception e) {
            log.error("Failed to update user password: {}", e.getMessage());
            throw new PasswordResetException("Failed to update password in database", e);
        }
    }

    /**
     * Handles and logs password reset errors.
     */
    private void handlePasswordResetError(Exception e) {
        AuthUser currentUser = userService.authenticatedUser();
        String userEmail = currentUser != null ? maskEmail(currentUser.getEmail()) : "unknown";

        log.error("Password reset failed for user: {}. Error: {}", userEmail, e.getMessage());
    }

    /**
     * Masks email for secure logging.
     */
    private String maskEmail(String email) {
        if (email == null || !email.contains("@")) {
            return "****";
        }

        String[] parts = email.split("@");
        String name = parts[0];
        String domain = parts[1];

        String maskedName = name.length() > 2
                ? name.substring(0, 2) + "****"
                : "****";

        return maskedName + "@" + domain;
    }
}