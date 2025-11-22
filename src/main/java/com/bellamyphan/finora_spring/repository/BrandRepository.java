package com.bellamyphan.finora_spring.repository;

import com.bellamyphan.finora_spring.entity.Brand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BrandRepository extends JpaRepository<Brand, String> {
    // JpaRepository provides basic CRUD operations

    List<Brand> findByNameContainingIgnoreCase(String name);

    boolean existsByNameIgnoreCaseAndLocationIgnoreCase(String name, String location);
}
