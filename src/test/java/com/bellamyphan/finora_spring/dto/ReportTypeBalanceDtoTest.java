package com.bellamyphan.finora_spring.dto;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class ReportTypeBalanceDtoTest {

    @Test
    void testConstructorAndGetters() {
        BigDecimal amount = new BigDecimal("123.45");
        ReportTypeBalanceDto dto = new ReportTypeBalanceDto("type1", amount);

        assertEquals("type1", dto.typeId());
        assertEquals(amount, dto.totalAmount());
    }

    @Test
    void testEqualityAndHashCode() {
        BigDecimal amount1 = new BigDecimal("100.00");
        BigDecimal amount2 = new BigDecimal("100.00");

        ReportTypeBalanceDto dto1 = new ReportTypeBalanceDto("t1", amount1);
        ReportTypeBalanceDto dto2 = new ReportTypeBalanceDto("t1", amount2);

        // Records with same values should be equal
        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    void testToString() {
        ReportTypeBalanceDto dto = new ReportTypeBalanceDto("tX", BigDecimal.TEN);
        String str = dto.toString();

        assertTrue(str.contains("tX"));
        assertTrue(str.contains("10"));
    }
}
