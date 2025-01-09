package com.dennis.user.service.controller;

import com.dennis.user.service.dto.UserDTO;
import com.dennis.user.service.exception.UserNotFoundException;
import com.dennis.user.service.model.Admin;
import com.dennis.user.service.model.Trainee;
import com.dennis.user.service.model.Trainer;
import com.dennis.user.service.model.User;
import com.dennis.user.service.model.enums.Role;
import com.dennis.user.service.service.ProfileService;
import com.dennis.user.service.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/users")
@Validated
public class UserController {
    private final UserService userService;
    private final ProfileService profileService;

    public UserController(UserService userService, ProfileService profileService) {
        this.userService = userService;
        this.profileService = profileService;
    }

    @PostMapping("/trainee/create")
    public ResponseEntity<Map<String, String>> createTrainee(@Valid @ModelAttribute Trainee trainee) {
        Long userId = userService.generateUserId();

        userService.createUser(trainee.getFirstName(),
                trainee.getLastName(),
                trainee.getEmail(),
                trainee.getStatus(),
                Role.TRAINEE,
                userId);
        trainee.setRole(Role.TRAINEE);
        trainee.setTrainingId(userId);

        profileService.sendTraineeProfile(trainee);
        return new ResponseEntity<>(Map.of("message", "Trainee Created"), HttpStatus.CREATED);
    }

    @PostMapping("/trainer/create")
    public ResponseEntity<Map<String, String>> createTrainer(@Valid @ModelAttribute Trainer trainer) {
        userService.createUser(trainer.getFirstName(),
                trainer.getLastName(),
                trainer.getEmail(),
                trainer.getStatus(),
                Role.TRAINER,
                null);
        trainer.setRole(Role.TRAINER);

        profileService.sendTrainerProfile(trainer);
        return new ResponseEntity<>(Map.of("message", "Trainer Created"), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return new ResponseEntity<>(userService.getAllUsers(), HttpStatus.OK);
    }

    @GetMapping("/role")
    public ResponseEntity<List<UserDTO>> getUsersByRole(@Valid @RequestParam Role role) {
        List<UserDTO> users = userService.getUsersByRole(role);
        if (users.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @PutMapping("/deactivate")
    public ResponseEntity<Map<String, String>> deactivateUser(@RequestParam @Email(message = "Invalid email format") String email) {
        try {
            String message = userService.deactivateUser(email);
            return new ResponseEntity<>(Map.of("message", message), HttpStatus.OK);
        } catch (UserNotFoundException e) {
            return new ResponseEntity<>(Map.of("error", "User not found"), HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/activate")
    public ResponseEntity<Map<String, String>> activateUser(@RequestParam @Email(message = "Invalid email format") String email) {
        try {
            String message = userService.activateUser(email);
            return new ResponseEntity<>(Map.of("message", message), HttpStatus.OK);
        } catch (UserNotFoundException e) {
            return new ResponseEntity<>(Map.of("error", "User not found"), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/curriculum-creator")
    public ResponseEntity<String> checkEmail(@RequestParam @Email(message = "Invalid email format") String email) {

        Optional<User> user = userService.findByEmail(email);
        if (user.isPresent()) {
            return ResponseEntity.ok(user.get().getFirstName() + " " + user.get().getLastName());
        }

        Optional<Admin> admin = userService.findAdminByEmail(email);
        if (admin.isPresent()) {
            return ResponseEntity.ok("ADMIN");
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Email not found");
    }


}