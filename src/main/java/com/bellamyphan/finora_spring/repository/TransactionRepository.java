package com.bellamyphan.finora_spring.repository;

import com.bellamyphan.finora_spring.entity.Transaction;
import com.bellamyphan.finora_spring.entity.TransactionGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, String> {
    // JpaRepository provides basic CRUD operations

    List<Transaction> findByGroup(TransactionGroup group);

    // Calculate total balance for a bank
    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.bank.id = :bankId")
    BigDecimal calculateBankBalance(@Param("bankId") String bankId);
}
