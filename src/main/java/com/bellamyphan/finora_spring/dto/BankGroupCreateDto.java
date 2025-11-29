package com.bellamyphan.finora_spring.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BankGroupCreateDto {
    @NotBlank(message = "Bank group name is required")
    String name;
}
