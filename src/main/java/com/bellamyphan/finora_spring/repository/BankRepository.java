package com.bellamyphan.finora_spring.repository;

import com.bellamyphan.finora_spring.entity.Bank;
import com.bellamyphan.finora_spring.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BankRepository extends JpaRepository<Bank, String> {

    List<Bank> findByUser(User user);

    @Query("""
    SELECT b
    FROM Bank b
    WHERE b.user.id = :userId
      AND b.openingDate <= :endOfMonth
      AND (b.closingDate IS NULL OR b.closingDate >= :startOfMonth)
      """)
    List<Bank> findActiveBanksInMonth(
            @Param("userId") String userId,
            @Param("startOfMonth") LocalDate startOfMonth,
            @Param("endOfMonth") LocalDate endOfMonth
    );
}
