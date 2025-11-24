package com.bellamyphan.finora_spring.repository;

import com.bellamyphan.finora_spring.entity.PendingTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PendingTransactionRepository extends JpaRepository<PendingTransaction, String> {

    boolean existsByTransactionId(String transactionId);

    void deleteByTransactionId(String transactionId);
}
