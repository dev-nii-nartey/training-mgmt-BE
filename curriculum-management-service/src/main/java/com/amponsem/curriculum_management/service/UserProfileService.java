package com.amponsem.curriculum_management.service;

import com.amponsem.curriculum_management.dto.UserDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@Slf4j
public class UserProfileService {

    @Value("${services.user-profile.base-url}")
    private String userProfileBaseUrl;
    private final AuthUserService authUserService;

    public UserProfileService(AuthUserService authUserService, WebClient.Builder webClientBuilder) {
        this.authUserService = authUserService;
    }

    public UserDTO getUserDetails(String userEmail) {
      return UserDTO.builder()
              .email(userEmail)
              .username(authUserService.getUserRole()).build();
    }
}
