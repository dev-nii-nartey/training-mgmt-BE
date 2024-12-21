package com.amponsem.authentication_service.dto.security;

import com.amponsem.authentication_service.model.AuthUser;
import com.amponsem.authentication_service.model.Role;
import com.amponsem.authentication_service.repository.AuthRepository;
import com.amponsem.authentication_service.services.CustomUserDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(UserDetailsServiceImpl.class);
    private final AuthRepository authRepository;

    public UserDetailsServiceImpl(AuthRepository authRepository) {
        this.authRepository = authRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        AuthUser authUser = authRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
        Role role = authUser.getRole();
        return new CustomUserDetails(authUser.getEmail(), authUser.getPassword(), role);

    }
}
