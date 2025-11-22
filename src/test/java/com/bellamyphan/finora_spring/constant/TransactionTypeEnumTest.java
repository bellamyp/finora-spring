package com.bellamyphan.finora_spring.constant;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TransactionTypeEnumTest {

    // ----------------------------
    // 1️⃣ Test valid parsing
    // ----------------------------
    @Test
    void testFromNameValid() {
        assertEquals(TransactionTypeEnum.INCOME, TransactionTypeEnum.fromName("Income"));
        assertEquals(TransactionTypeEnum.HEALTH, TransactionTypeEnum.fromName("health")); // case-insensitive
        assertEquals(TransactionTypeEnum.CAR, TransactionTypeEnum.fromName("Car"));
    }

    // ----------------------------
    // 2️⃣ Test invalid parsing
    // ----------------------------
    @Test
    void testFromNameInvalid() {
        Exception ex = assertThrows(IllegalArgumentException.class,
                () -> TransactionTypeEnum.fromName("Unknown"));

        assertTrue(ex.getMessage().contains("Invalid TransactionTypeEnum name"));
    }

    // ----------------------------
    // 3️⃣ Optional: Test enum size (ensures no accidental changes)
    // ----------------------------
    @Test
    void testEnumCount() {
        assertEquals(22, TransactionTypeEnum.values().length); // update count if enum changes
    }
}
