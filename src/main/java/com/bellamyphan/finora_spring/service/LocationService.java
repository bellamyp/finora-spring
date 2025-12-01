package com.bellamyphan.finora_spring.service;

import com.bellamyphan.finora_spring.dto.LocationCreateDto;
import com.bellamyphan.finora_spring.dto.LocationDto;
import com.bellamyphan.finora_spring.entity.Location;
import com.bellamyphan.finora_spring.repository.LocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LocationService {

    private final LocationRepository locationRepository;
    private final NanoIdService nanoIdService;

    /**
     * Fetch all locations ordered by city ascending
     */
    public List<LocationDto> getAllLocations() {
        return locationRepository.findAllByOrderByCityAsc()
                .stream()
                .map(LocationDto::fromEntity)
                .toList();
    }

    /**
     * Create a new location, check for duplicates (ignore case)
     */
    @Transactional
    public LocationDto createLocation(LocationCreateDto request) {
        String cityNormalized = request.getCity().trim();
        String stateNormalized = request.getState().trim();

        // Check for duplicate
        if (locationRepository.existsByCityIgnoreCaseAndStateIgnoreCase(cityNormalized, stateNormalized)) {
            throw new IllegalArgumentException("Location already exists: " + cityNormalized + ", " + stateNormalized);
        }

        // Create and save
        Location location = new Location(cityNormalized, stateNormalized);
        location.setId(nanoIdService.generateUniqueId(locationRepository));
        return LocationDto.fromEntity(locationRepository.save(location));
    }
}
