package com.bellamyphan.finora_spring.repository;

import com.bellamyphan.finora_spring.entity.BankType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

//import java.util.Optional;

@Repository
public interface BankTypeRepository extends JpaRepository<BankType, String> {
    // basic CRUD included

//    Optional<BankType> findByType(String type);
}
