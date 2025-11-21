package com.bellamyphan.finora_spring.dto;

import com.bellamyphan.finora_spring.constant.TransactionTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
@AllArgsConstructor
public class TransactionDto {
    private UUID id;
    private LocalDate date;
    private Double amount;
    private TransactionTypeEnum type;
    private String notes;
    private String bankName;
    private String userEmail;
}