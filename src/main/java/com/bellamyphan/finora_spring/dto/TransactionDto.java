package com.bellamyphan.finora_spring.dto;

import com.bellamyphan.finora_spring.entity.TransactionTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class TransactionDto {
    private Long id;
    private LocalDate date;
    private Double amount;
    private TransactionTypeEnum type;
    private String notes;
    private String bankName;
    private String userEmail;
}