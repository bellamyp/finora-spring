package com.bellamyphan.finora_spring.repository;

import com.bellamyphan.finora_spring.entity.Bank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BankRepository extends JpaRepository<Bank, Long> {
    // basic CRUD included

    List<Bank> findByUserEmail(String email);  // filter by user's email
}
