package com.amponsem.authentication_service.services;

import com.amponsem.authentication_service.dto.AuthUserDTO;
import com.amponsem.authentication_service.exception.UserNotFoundException;
import com.amponsem.authentication_service.model.AuthUser;
import com.amponsem.authentication_service.repository.AuthRepository;
import com.amponsem.authentication_service.util.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserService {

    private final AuthRepository authRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final HttpServletRequest request;

    public UserService(AuthRepository authRepository, JwtTokenProvider jwtTokenProvider, HttpServletRequest request) {
        this.authRepository = authRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.request = request;
    }

    public void createAuthUser(AuthUserDTO authUserDTO) {
        AuthUser authUser = AuthUser.builder()
                .email(authUserDTO.getEmail())
                .password(authUserDTO.getPassword())
                .role(authUserDTO.getRole())
                .build();
       authRepository.save(authUser);
    }

    public AuthUser authenticatedUser() {
        String token = jwtTokenProvider.resolveToken(request);
        return authRepository.findByEmail(jwtTokenProvider.getUsername(token)).orElseThrow(() -> new UserNotFoundException("User not Authenticated"));
    }
}
