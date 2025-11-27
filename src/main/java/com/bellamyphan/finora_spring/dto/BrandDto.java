package com.bellamyphan.finora_spring.dto;

import com.bellamyphan.finora_spring.entity.Brand;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Optional;

@Data
@AllArgsConstructor
public class BrandDto {

    @NotBlank(message = "Brand id is required")
    private String id;

    @NotBlank(message = "Brand name is required")
    private String name;

    private String url;

    // ==========================
    // Convert Entity → DTO
    // ==========================
    public static BrandDto fromEntity(Brand brand) {
        return Optional.ofNullable(brand)
                .map(b -> new BrandDto(b.getId(), b.getName(), b.getUrl()))
                .orElse(null);
    }

    // ==========================
    // Convert DTO → Entity
    // ==========================
    public Brand toEntity() {
        Brand brand = new Brand(name, url);
        brand.setId(id);
        return brand;
    }
}