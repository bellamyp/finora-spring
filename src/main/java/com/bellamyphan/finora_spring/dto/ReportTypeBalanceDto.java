package com.bellamyphan.finora_spring.dto;

import java.math.BigDecimal;

public record ReportTypeBalanceDto(
        String typeId,
        BigDecimal totalAmount
) {}