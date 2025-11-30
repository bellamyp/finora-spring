package com.bellamyphan.finora_spring.repository;

import com.bellamyphan.finora_spring.entity.TransactionGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionGroupRepository extends JpaRepository<TransactionGroup, String> {

    @Query("""
        SELECT tg
        FROM TransactionGroup tg
        JOIN tg.transactions t
        JOIN t.bank b
        WHERE tg.report IS NULL
          AND b.user.id = :userId
    """)
    List<TransactionGroup> findCandidateGroups(@Param("userId") String userId);

    // Fetch all groups assigned to a specific report
    List<TransactionGroup> findByReportId(String reportId);
}
