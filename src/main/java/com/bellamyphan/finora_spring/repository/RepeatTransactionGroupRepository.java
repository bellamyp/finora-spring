package com.bellamyphan.finora_spring.repository;

import com.bellamyphan.finora_spring.entity.RepeatTransactionGroup;
import com.bellamyphan.finora_spring.entity.TransactionGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RepeatTransactionGroupRepository
        extends JpaRepository<RepeatTransactionGroup, TransactionGroup> {
    // No extra methods needed for basic CRUD
}
