package com.bellamyphan.finora_spring.repository;

import com.bellamyphan.finora_spring.entity.Brand;
import com.bellamyphan.finora_spring.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BrandRepository extends JpaRepository<Brand, String> {

    // Search by name (case-insensitive)
    List<Brand> findByNameContainingIgnoreCase(String name);

    // Check if a brand with same name + location + user exists
    boolean existsByNameIgnoreCaseAndLocationIgnoreCaseAndUser(String name, String location, User user);
}
