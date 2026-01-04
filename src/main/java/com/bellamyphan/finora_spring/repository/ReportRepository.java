package com.bellamyphan.finora_spring.repository;

import com.bellamyphan.finora_spring.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReportRepository extends JpaRepository<Report, String> {
    // Find latest report for a user
    Optional<Report> findTopByUserIdOrderByMonthDesc(String userId);

    Optional<Report> findById(String id);

    // All reports for a user, sorted by month descending
    List<Report> findAllByUserIdOrderByMonthDesc(String userId);

    // Check if user has at least 1 pending report
    boolean existsByUserIdAndIsPostedFalse(String userId);
}
