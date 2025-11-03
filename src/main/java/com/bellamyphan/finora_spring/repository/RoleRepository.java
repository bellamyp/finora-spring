package com.bellamyphan.finora_spring.repository;

import com.bellamyphan.finora_spring.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RoleRepository extends JpaRepository<Role, UUID> {
    // JpaRepository provides basic CRUD operations

    Optional<Role> findByName(String name);
}