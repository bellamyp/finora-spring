package com.bellamyphan.finora_spring.controller;

import com.bellamyphan.finora_spring.dto.LocationCreateDto;
import com.bellamyphan.finora_spring.dto.LocationDto;
import com.bellamyphan.finora_spring.service.LocationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/locations")
public class LocationController {

    private final LocationService locationService;

    // -------------------------
    // CREATE LOCATION
    // -------------------------
    @PostMapping
    public ResponseEntity<LocationDto> createLocation(@Valid @RequestBody LocationCreateDto request) {
        LocationDto response = locationService.createLocation(request);
        return ResponseEntity.ok(response);
    }

    // -------------------------
    // GET ALL LOCATIONS
    // -------------------------
    @GetMapping
    public ResponseEntity<List<LocationDto>> getLocations() {
        List<LocationDto> locations = locationService.getAllLocations();
        return ResponseEntity.ok(locations);
    }
}
