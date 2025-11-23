package com.bellamyphan.finora_spring.dto;

import lombok.Data;

import java.util.List;

@Data
public class TransactionGroupCreateDto {
    private String date;          // "yyyy-MM-dd"
    private String brandId;       // NanoID
    private String typeId;        // Enum name
    private List<TransactionCreateDto> transactions;
}
