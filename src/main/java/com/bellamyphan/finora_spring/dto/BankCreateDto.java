package com.bellamyphan.finora_spring.dto;

import com.bellamyphan.finora_spring.constant.BankTypeEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class BankCreateDto {

    @NotBlank(message = "Bank name is required")
    private String name;

    @NotNull(message = "Bank must have an opening date")
    private LocalDate openingDate;

    // Optional closing date, can mark as closing later
    private LocalDate closingDate;

    @NotBlank(message = "Bank must belong to a bank group")
    private String groupId;

    @NotNull(message = "Bank must have a type")
    private BankTypeEnum type; // Enum representing bank type
}