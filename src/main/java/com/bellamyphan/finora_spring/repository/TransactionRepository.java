package com.bellamyphan.finora_spring.repository;

import com.bellamyphan.finora_spring.entity.Transaction;
import com.bellamyphan.finora_spring.entity.TransactionGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, String> {

    List<Transaction> findByGroup(TransactionGroup group);

    @Query("""
        SELECT t
        FROM Transaction t
        JOIN t.bank b
        WHERE b.user.id = :userId
        """)
    List<Transaction> findByUserId(@Param("userId") String userId);

    @Query("""
            SELECT t
            FROM Transaction t
            JOIN t.bank b
            WHERE b.user.id = :userId
              AND EXISTS (
                    SELECT 1
                    FROM PendingTransaction p
                    WHERE p.transaction.id = t.id
              )
            """)
    List<Transaction> findPendingByUserId(@Param("userId") String userId);

    List<Transaction> findByBankIdAndDateBetweenOrderByDateAsc(
            String bankId, LocalDate start, LocalDate end);

    @Query("""
        SELECT t
        FROM Transaction t
        JOIN t.bank b
        WHERE t.group = :group
          AND b.user.id = :userId
        """)
    List<Transaction> findByGroupAndBankUserId(
            @Param("group") TransactionGroup group,
            @Param("userId") String userId
    );

    @Query("""
    SELECT COALESCE(SUM(t.amount), 0)
    FROM Transaction t
    WHERE t.bank.id = :bankId
    """)
    BigDecimal calculatePendingBankBalance(@Param("bankId") String bankId);

    @Query("""
    SELECT COALESCE(SUM(t.amount), 0)
    FROM Transaction t
    WHERE t.bank.id = :bankId
      AND NOT EXISTS (
            SELECT 1
            FROM PendingTransaction p
            WHERE p.transaction.id = t.id
      )
    """)
    BigDecimal calculatePostedBankBalance(@Param("bankId") String bankId);

    @Query("""
    SELECT COALESCE(SUM(t.amount), 0)
    FROM Transaction t
    WHERE t.bank.id = :bankId
      AND t.date < :startDate
    """)
    BigDecimal calculateBalanceBeforeDate(
            @Param("bankId") String bankId,
            @Param("startDate") LocalDate startDate
    );
}
