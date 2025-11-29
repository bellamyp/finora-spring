package com.bellamyphan.finora_spring.repository;

import com.bellamyphan.finora_spring.entity.BankGroup;
import com.bellamyphan.finora_spring.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BankGroupRepository extends JpaRepository<BankGroup, String> {

    boolean existsByNameAndUser(String name, User user);

    List<BankGroup> findAllByUser(User user);
}