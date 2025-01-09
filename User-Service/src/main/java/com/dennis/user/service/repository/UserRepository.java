package com.dennis.user.service.repository;

import com.dennis.user.service.model.User;
import com.dennis.user.service.model.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    List<User> findByRole(Role role);
    Optional<User> findByUserId(Long trainingId);
}
