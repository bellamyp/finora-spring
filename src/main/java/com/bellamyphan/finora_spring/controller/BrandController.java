package com.bellamyphan.finora_spring.controller;

import com.bellamyphan.finora_spring.dto.BrandCreateDto;
import com.bellamyphan.finora_spring.dto.BrandDto;
import com.bellamyphan.finora_spring.service.BrandService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/brands")
public class BrandController {

    private final BrandService brandService;

    // -------------------------
    // CREATE BRAND
    // -------------------------
    @PostMapping
    public ResponseEntity<BrandDto> createBrand(@Valid @RequestBody BrandCreateDto request) {
        BrandDto response = brandService.createBrand(request);
        return ResponseEntity.ok(response);
    }

    // -------------------------
    // SEARCH BRAND BY NAME
    // -------------------------
    @GetMapping("/search")
    public ResponseEntity<?> searchBrandByName(@RequestParam String name) {
        if (name == null || name.isBlank()) {
            return ResponseEntity.badRequest().body("Search name cannot be empty.");
        }

        return ResponseEntity.ok(brandService.searchByName(name));
    }
}
