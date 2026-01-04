package com.bellamyphan.finora_spring.dto;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ReportBankBalanceDtoTest {

    @Test
    void recordShouldStoreValuesCorrectly() {
        // Arrange
        String bankId = "bank123";
        BigDecimal totalAmount = BigDecimal.valueOf(1000.50);
        String bankName = "Bank A";
        String bankGroupName = "Group X";

        // Act
        ReportBankBalanceDto dto = new ReportBankBalanceDto(bankId, totalAmount, bankName, bankGroupName);

        // Assert
        assertEquals(bankId, dto.bankId());
        assertEquals(totalAmount, dto.totalAmount());
        assertEquals(bankName, dto.bankName());
        assertEquals(bankGroupName, dto.bankGroupName());
    }
}
