package com.bellamyphan.finora_spring.repository;

import com.bellamyphan.finora_spring.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LocationRepository extends JpaRepository<Location, String> {

    boolean existsByCityIgnoreCaseAndStateIgnoreCase(String city, String state);

    // Fetch all locations ordered by city ascending
    List<Location> findAllByOrderByCityAsc();
}
