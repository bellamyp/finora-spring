package com.bellamyphan.finora_spring.repository;

import com.bellamyphan.finora_spring.entity.Bank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BankRepository extends JpaRepository<Bank, UUID> {
    // basic CRUD included

    List<Bank> findByUser_Email(String email);
}
