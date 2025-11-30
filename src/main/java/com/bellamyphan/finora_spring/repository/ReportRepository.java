package com.bellamyphan.finora_spring.repository;

import com.bellamyphan.finora_spring.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReportRepository extends JpaRepository<Report, String> {
    // Find latest report for a user
    Optional<Report> findTopByUserIdOrderByMonthDesc(String userId);
}
