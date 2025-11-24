package com.bellamyphan.finora_spring.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransactionSearchDto {
    private String startDate;
    private String endDate;
    private BigDecimal minAmount;
    private BigDecimal maxAmount;
    private String bankId;
    private String brandId;
    private String typeId;
    private String keyword;
}
