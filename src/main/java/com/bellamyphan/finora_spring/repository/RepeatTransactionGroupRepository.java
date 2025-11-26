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
        SELECT DISTINCT r
        FROM RepeatTransactionGroup r
        JOIN Transaction t ON t.group = r.group
        JOIN t.bank b
        WHERE b.user.id = :userId
    """)
    List<RepeatTransactionGroup> findByUserId(@Param("userId") String userId);
}
