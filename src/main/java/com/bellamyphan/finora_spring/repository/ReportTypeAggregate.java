package com.bellamyphan.finora_spring.repository;

import java.math.BigDecimal;

/**
 * Projection interface for live type balance aggregation
 */
public interface ReportTypeAggregate {
    String getTypeId();
    BigDecimal getTotalAmount();
}
