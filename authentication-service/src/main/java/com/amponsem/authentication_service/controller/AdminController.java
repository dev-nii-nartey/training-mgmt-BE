package com.amponsem.authentication_service.controller;

import com.amponsem.authentication_service.dto.AuthUserDTO;
import com.amponsem.authentication_service.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
public class AdminController {
    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping(("/add"))
    public ResponseEntity<String> registerAdmin(@RequestBody AuthUserDTO authUserDTO) {
       userService.createAuthUser(authUserDTO);
        return ResponseEntity.ok("User registered in Auth Service");
    }

}
