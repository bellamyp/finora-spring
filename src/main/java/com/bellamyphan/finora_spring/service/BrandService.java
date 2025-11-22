package com.bellamyphan.finora_spring.service;

import com.bellamyphan.finora_spring.dto.BrandCreateDto;
import com.bellamyphan.finora_spring.dto.BrandDto;
import com.bellamyphan.finora_spring.entity.Brand;
import com.bellamyphan.finora_spring.repository.BrandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BrandService {

    private final BrandRepository brandRepository;
    private final NanoIdService nanoIdService;

    public List<BrandDto> searchByName(String name) {
        return brandRepository.findByNameContainingIgnoreCase(name)
                .stream()
                .map(this::toDto)
                .toList();
    }

    public BrandDto createBrand(BrandCreateDto request) {

        // Prevent duplicate brand with same name AND same location
        if (brandRepository.existsByNameIgnoreCaseAndLocationIgnoreCase(
                request.getName(),
                request.getLocation()
        )) {
            throw new IllegalArgumentException(
                    "Brand '" + request.getName() + "' at location '" +
                            request.getLocation() + "' already exists."
            );
        }

        Brand brand = new Brand(request.getName(), request.getLocation());

        for (int i = 0; i < 10; i++) {
            try {
                brand.setId(nanoIdService.generate());
                return toDto(brandRepository.save(brand)); // success
            } catch (DataIntegrityViolationException ignored) {
                // retry if collision
            }
        }

        throw new RuntimeException("Failed to generate unique Brand ID after 10 attempts");
    }

    /**
     * Convert Brand → BrandDto
     */
    private BrandDto toDto(Brand brand) {
        return new BrandDto(
                brand.getId(),
                brand.getName(),
                brand.getLocation()
        );
    }
}
