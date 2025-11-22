package com.bellamyphan.finora_spring.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BrandDto {
    private String id;
    private String name;
    private String location;
}