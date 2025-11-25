package com.bellamyphan.finora_spring.repository;

import com.bellamyphan.finora_spring.entity.RepeatTransactionGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RepeatTransactionGroupRepository
        extends JpaRepository<RepeatTransactionGroup, String> {
    // No extra methods needed for basic CRUD
}
