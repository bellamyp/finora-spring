package com.bellamyphan.finora_spring.dto;

import lombok.Data;
import java.util.List;

@Data
public class TransactionGroupCreateDto {
    private List<TransactionCreateDto> transactions;
}
