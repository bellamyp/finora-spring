package com.bellamyphan.finora_spring.repository;

import com.bellamyphan.finora_spring.entity.TransactionGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionGroupRepository extends JpaRepository<TransactionGroup, String> {
    // JpaRepository provides basic CRUD operations
}
