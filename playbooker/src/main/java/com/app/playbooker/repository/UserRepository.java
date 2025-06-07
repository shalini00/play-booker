package com.app.playbooker.repository;

import com.app.playbooker.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User,String> {
    User findByEmail(String email);
    User findByPhoneNumber(String phoneNumber);
    Optional<User> findByUsername(String name);
    boolean existsByUsername(String username);
    Long countByCreatedAtAfter(LocalDateTime fromDate);
}
