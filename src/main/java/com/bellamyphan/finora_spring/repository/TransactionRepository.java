package com.bellamyphan.finora_spring.repository;

import com.bellamyphan.finora_spring.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
    // JpaRepository provides basic CRUD operations

    List<Transaction> findByUser_Email(String email);
}
