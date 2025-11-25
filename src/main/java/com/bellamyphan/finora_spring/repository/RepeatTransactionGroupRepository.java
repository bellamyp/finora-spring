package com.bellamyphan.finora_spring.repository;

import java.util.List;

import com.bellamyphan.finora_spring.entity.RepeatTransactionGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RepeatTransactionGroupRepository
        extends JpaRepository<RepeatTransactionGroup, String> {
    // No extra methods needed for basic CRUD

    @Query("""
    SELECT r
    FROM RepeatTransactionGroup r
    JOIN r.group g
    JOIN g.transactions t
    WHERE t.bank.user.id = :userId
""")
    List<RepeatTransactionGroup> findByUserId(@Param("userId") String userId);
}
