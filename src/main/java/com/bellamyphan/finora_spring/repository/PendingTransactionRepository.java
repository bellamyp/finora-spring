package com.bellamyphan.finora_spring.repository;

import com.bellamyphan.finora_spring.entity.PendingTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface PendingTransactionRepository extends JpaRepository<PendingTransaction, String> {

    boolean existsByTransactionId(String transactionId);

    // Efficiently load all pending transaction IDs
    @Query("SELECT p.transaction.id FROM PendingTransaction p")
    Set<String> findAllTransactionIds();

    void deleteByTransactionId(String transactionId);
}
