package com.bellamyphan.finora_spring.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransactionResponseDto {
    private String id;
    private String date;
    private BigDecimal amount;
    private String notes;
    private String bankId;
    private String brandId;
    private String typeId;
}
