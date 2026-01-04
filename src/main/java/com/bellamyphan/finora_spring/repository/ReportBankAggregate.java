package com.bellamyphan.finora_spring.repository;

import java.math.BigDecimal;

/**
 * Projection interface for live bank balance aggregation
 */
public interface ReportBankAggregate {
    String getBankId();
    BigDecimal getTotalAmount();
}