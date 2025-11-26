package com.bellamyphan.finora_spring.controller;

import com.bellamyphan.finora_spring.dto.BrandCreateDto;
import com.bellamyphan.finora_spring.dto.BrandDto;
import com.bellamyphan.finora_spring.service.BrandService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    // GET ALL BRANDS
    // -------------------------
    @GetMapping
    public ResponseEntity<List<BrandDto>> getBrands() {
        return ResponseEntity.ok(brandService.getAllBrands());
    }
}
