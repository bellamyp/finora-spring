package com.bellamyphan.finora_spring.service;

import com.bellamyphan.finora_spring.dto.BrandCreateDto;
import com.bellamyphan.finora_spring.dto.BrandDto;
import com.bellamyphan.finora_spring.entity.Brand;
import com.bellamyphan.finora_spring.repository.BrandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BrandService {

    private final BrandRepository brandRepository;
    private final NanoIdService nanoIdService;

    /**
     * Fetch all brands
     */
    public List<BrandDto> getAllBrands() {
        return brandRepository.findAllByOrderByNameAsc()
                .stream()
                .map(BrandDto::fromEntity)
                .toList();
    }

    public BrandDto createBrand(BrandCreateDto request) {
        // Normalize name and URL
        String normalizedName = request.getName().trim();
        String urlNormalized = request.getUrl() != null ? request.getUrl().trim().toLowerCase() : null;

        // Check for name or url duplication
        if (brandRepository.existsByNameIgnoreCase(normalizedName)) {
            throw new IllegalArgumentException("Brand name already exists: '" + normalizedName + "'");
        }
        if (urlNormalized != null && brandRepository.existsByUrlIgnoreCase(urlNormalized)) {
            throw new IllegalArgumentException("Brand URL already exists: '" + urlNormalized + "'");
        }

        // Create brand and save to DB.
        Brand brand = new Brand(normalizedName, urlNormalized);
        brand.setId(nanoIdService.generateUniqueId(brandRepository));
        return BrandDto.fromEntity(brandRepository.save(brand));
    }
}
