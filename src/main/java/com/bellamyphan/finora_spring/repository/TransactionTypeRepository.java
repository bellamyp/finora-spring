package com.bellamyphan.finora_spring.repository;

import com.bellamyphan.finora_spring.entity.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TransactionTypeRepository extends JpaRepository<TransactionType, UUID> {
    // JpaRepository provides basic CRUD operations

    Optional<TransactionType> findByType(String type); // lookup by type string
}
