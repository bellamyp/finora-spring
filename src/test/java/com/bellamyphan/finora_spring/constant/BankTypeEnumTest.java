package com.bellamyphan.finora_spring.constant;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BankTypeEnumTest {

    // ----------------------------
    // 1️⃣ Test real fromName behavior
    // ----------------------------
    @Test
    void testFromNameValid() {
        assertEquals(BankTypeEnum.CHECKING, BankTypeEnum.fromName("CHECKING"));
        assertEquals(BankTypeEnum.CHECKING, BankTypeEnum.fromName("checking")); // case-insensitive
        assertEquals(BankTypeEnum.SAVINGS, BankTypeEnum.fromName("Savings"));
        assertEquals(BankTypeEnum.CREDIT, BankTypeEnum.fromName("CREDIT"));
        assertEquals(BankTypeEnum.REWARDS, BankTypeEnum.fromName("rewards"));
    }

    // ----------------------------
    // 2️⃣ Test invalid input throws exception
    // ----------------------------
    @Test
    void testFromNameInvalid() {
        Exception ex = assertThrows(IllegalArgumentException.class,
                () -> BankTypeEnum.fromName("something-else"));

        assertTrue(ex.getMessage().contains("No matching BankTypeEnum"));
    }

    // ----------------------------
    // 3️⃣ Test enum constants exist as expected
    // ----------------------------
    @Test
    void testEnumValues() {
        BankTypeEnum[] values = BankTypeEnum.values();

        assertEquals(4, values.length);
        assertSame(BankTypeEnum.CHECKING, values[0]);
        assertSame(BankTypeEnum.SAVINGS, values[1]);
        assertSame(BankTypeEnum.CREDIT, values[2]);
        assertSame(BankTypeEnum.REWARDS, values[3]);
    }
}
