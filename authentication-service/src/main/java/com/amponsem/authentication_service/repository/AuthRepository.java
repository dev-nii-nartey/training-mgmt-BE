package com.amponsem.authentication_service.repository;

import com.amponsem.authentication_service.model.AuthUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthRepository extends JpaRepository<AuthUser, Long> {
    Optional<AuthUser> findByEmail(String email);

    Optional<AuthUser> findByOtp(String otp);
}


