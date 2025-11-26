package com.bellamyphan.finora_spring.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BrandCreateDto {

    @NotBlank(message = "Brand name is required")
    private String name;

    private String url;
}
