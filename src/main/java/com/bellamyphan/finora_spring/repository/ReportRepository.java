package com.bellamyphan.finora_spring.repository;

import com.bellamyphan.finora_spring.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Optional;

public interface ReportRepository extends JpaRepository<Report, String> {

    // Find latest report for a user
    Optional<Report> findTopByUserIdOrderByMonthDesc(@NonNull String userId);

    // All reports for a user, sorted by month descending
    List<Report> findAllByUserIdOrderByMonthDesc(@NonNull String userId);

    // Check if user has at least 1 pending report
    boolean existsByUserIdAndIsPostedFalse(@NonNull String userId);

    // --------------------------
    // Live calculation of type balances
    // --------------------------
    @Query("""
        SELECT t.type.id AS typeId, SUM(t.amount) AS totalAmount
        FROM TransactionGroup tg
        JOIN tg.transactions t
        WHERE tg.report.id = :reportId
        GROUP BY t.type.id
    """)
    List<ReportTypeAggregate> calculateLiveTypeBalances(@Param("reportId") @NonNull String reportId);
}
