package com.bellamyphan.finora_spring.repository;

import com.bellamyphan.finora_spring.entity.Bank;
import com.bellamyphan.finora_spring.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BankRepository extends JpaRepository<Bank, String> {
    // basic CRUD included

    List<Bank> findByUser(User user);
}
