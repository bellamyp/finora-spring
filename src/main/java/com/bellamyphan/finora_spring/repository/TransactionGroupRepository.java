package com.bellamyphan.finora_spring.repository;

import com.bellamyphan.finora_spring.entity.Report;
import com.bellamyphan.finora_spring.entity.TransactionGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionGroupRepository extends JpaRepository<TransactionGroup, String> {
    // JpaRepository provides basic CRUD operations

    /**
     * Get fully posted transaction groups available for a new report
     * - Only groups where all transactions are posted (i.e., not in pending_transactions)
     * - Only groups not yet assigned to a report (report_id IS NULL)
     * - Optional: sorted by latest transaction date descending
     */
    @Query("""
        SELECT tg
        FROM TransactionGroup tg
        JOIN FETCH tg.transactions t
        JOIN t.bank b
        WHERE tg.report IS NULL
          AND b.user.id = :userId
          AND t.id NOT IN (SELECT pt.transactionId FROM PendingTransaction pt)
        GROUP BY tg
        HAVING COUNT(t) = (SELECT COUNT(tt) FROM Transaction tt WHERE tt.group = tg)
        ORDER BY MAX(t.date) DESC
    """)
    List<TransactionGroup> getFullyPostedGroupsForNewReport(@Param("userId") String userId);

    @Modifying
    @Query("UPDATE TransactionGroup tg SET tg.report = :report WHERE tg.id IN :groupIds")
    void assignGroupsToReport(@Param("report") Report report, @Param("groupIds") List<String> groupIds);
}
