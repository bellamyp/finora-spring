package com.bellamyphan.finora_spring.repository;

import com.bellamyphan.finora_spring.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    // JpaRepository provides basic CRUD operations
    // Additional custom queries can be defined here if needed

    // Find user by email (used for login/authentication)
    Optional<User> findByEmail(String email);
}
