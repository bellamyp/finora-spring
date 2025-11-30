package com.bellamyphan.finora_spring.dto;

import lombok.Data;

import java.util.List;

@Data
public class TransactionGroupResponseDto {
    private String id;
    private String reportId;
    private List<TransactionResponseDto> transactions;
}

