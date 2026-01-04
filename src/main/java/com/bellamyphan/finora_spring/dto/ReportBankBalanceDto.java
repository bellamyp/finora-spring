package com.bellamyphan.finora_spring.dto;

import java.math.BigDecimal;

public record ReportBankBalanceDto (
        String bankId,
        BigDecimal totalAmount,
        String bankName,
        String bankGroupName
) {}