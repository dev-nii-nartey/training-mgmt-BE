package com.dennis.user.service.service;


import com.dennis.user.service.config.RabbitConfig;
import com.dennis.user.service.dto.AuthUserDTO;
import com.dennis.user.service.dto.ProfileUpdateDTO;
import com.dennis.user.service.dto.UserDTO;
import com.dennis.user.service.exception.EmailAlreadyExistsException;
import com.dennis.user.service.exception.UserNotFoundException;
import com.dennis.user.service.model.Admin;
import com.dennis.user.service.model.Email;
import com.dennis.user.service.model.User;
import com.dennis.user.service.model.enums.Role;
import com.dennis.user.service.model.enums.Status;
import com.dennis.user.service.repository.AdminRepository;
import com.dennis.user.service.repository.UserRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;




@Service
public class UserService {
    private final UserRepository userRepository;
    private final RabbitTemplate rabbitTemplate;
    private final AuthService authService;
    private final PasswordService passwordService;
    private final ProfileService profileService;
    private final AdminRepository adminRepository;


    public UserService(UserRepository userRepository, RabbitTemplate rabbitTemplate, AuthService authService, PasswordService passwordService, ProfileService profileService, AdminRepository adminRepository) {
        this.userRepository = userRepository;
        this.rabbitTemplate = rabbitTemplate;
        this.authService = authService;
        this.passwordService = passwordService;
        this.profileService = profileService;
        this.adminRepository = adminRepository;
    }

    @Transactional
    public void createUser(String firstName, String lastName, String email, Status status, Role role, Long userId) {

        Optional<User> existingUserOptional = userRepository.findByEmail(email);

        if (existingUserOptional.isPresent()) {
            User existingUser = existingUserOptional.get();
            if (existingUser.getStatus() == Status.DEACTIVATED) {
                existingUser.setFirstName(firstName);
                existingUser.setLastName(lastName);
                existingUser.setRole(role);
                existingUser.setStatus(status);
                existingUser.setUserId(userId);
                existingUser.setUpdatedAt(LocalDateTime.now());
                userRepository.save(existingUser);
            } else {
                throw new EmailAlreadyExistsException("Email " + email + " is already associated with an account");
            }
        } else {
            User newUser = User.builder()
                    .firstName(firstName)
                    .lastName(lastName)
                    .email(email)
                    .role(role)
                    .status(status)
                    .userId(userId)
                    .createdAt(LocalDateTime.now())
                    .build();
            userRepository.save(newUser);
        }

        String password = passwordService.passwordGenerator();


        Email emailMessage = new Email(email, firstName, password);
        rabbitTemplate.convertAndSend(RabbitConfig.EMAIL_QUEUE, emailMessage);


        AuthUserDTO authUserDTO = AuthUserDTO.builder()
                .email(email)
                .password(passwordService.hashPassword(password))
                .isFirstTime(true)
                .role(role)
                .build();
        authService.sendToAuthService(authUserDTO);
    }


    @Transactional(readOnly = true)
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<UserDTO> getUsersByRole(Role role) {
        return userRepository.findByRole(role).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private UserDTO convertToDto(User user) {
        UserDTO userDto = new UserDTO();
        userDto.setFirstName(user.getFirstName());
        userDto.setLastName(user.getLastName());
        userDto.setEmail(user.getEmail());
        userDto.setRole(user.getRole());
        userDto.setStatus(user.getStatus());
        return userDto;
    }

    public String deactivateUser(String email) {
        ProfileUpdateDTO profileUpdateDTO = ProfileUpdateDTO.builder()
                .email(email)
                .status(Status.DEACTIVATED)
                .build();
        profileService.sendProfileUpdate(profileUpdateDTO);
        return updateUserStatus(email, 0);
    }

    public String activateUser(String email) {
        return updateUserStatus(email, 1);
    }

    private String updateUserStatus(String email, int statusFlag) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));

        Status newStatus = (statusFlag == 0) ? Status.DEACTIVATED : Status.ACTIVE;

        if (user.getStatus() == newStatus) {
            return String.format("User account is already %s.", newStatus.toString().toLowerCase());
        }

        user.setStatus(newStatus);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);

        return String.format("User account %s successfully.",
                newStatus == Status.DEACTIVATED ? "deactivated" : "activated");
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<Admin> findAdminByEmail(String email) {
        return adminRepository.findByEmail(email);
    }

    public Long generateUserId() {
        Long UserId;

        do {
            int randomDigits = 10000000 + new Random().nextInt(9000);
            UserId = (long) randomDigits;
        } while (isTrainingIdExists(UserId));

        return UserId;
    }


    private boolean isTrainingIdExists(Long trainingId) {
        return userRepository.findByUserId(trainingId).isPresent();
    }
}