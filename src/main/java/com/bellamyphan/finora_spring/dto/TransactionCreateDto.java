package com.bellamyphan.finora_spring.dto;

import lombok.Data;

@Data
public class TransactionCreateDto {
    private Double amount;
    private String bankId;
    private String notes;
}
