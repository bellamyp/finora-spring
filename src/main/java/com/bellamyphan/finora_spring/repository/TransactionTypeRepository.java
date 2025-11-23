package com.bellamyphan.finora_spring.repository;

import com.bellamyphan.finora_spring.constant.TransactionTypeEnum;
import com.bellamyphan.finora_spring.entity.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TransactionTypeRepository extends JpaRepository<TransactionType, String> {
    // JpaRepository provides basic CRUD operations

    Optional<TransactionType> findByType(TransactionTypeEnum type); // lookup by type string
}
