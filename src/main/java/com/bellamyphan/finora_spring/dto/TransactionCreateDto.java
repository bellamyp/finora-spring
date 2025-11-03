package com.bellamyphan.finora_spring.dto;

import com.bellamyphan.finora_spring.entity.TransactionTypeEnum;
import lombok.Data;

import java.time.LocalDate;

@Data
public class TransactionCreateDto {
    private LocalDate date;
    private Double amount;
    private TransactionTypeEnum type; // Enum for transaction type
    private String notes;
    private Long bankId;      // optional
    private String userEmail; // link transaction to user by email
}
