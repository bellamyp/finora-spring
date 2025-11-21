package com.bellamyphan.finora_spring.constant;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mockStatic;

@ExtendWith(MockitoExtension.class)
class TransactionTypeEnumTest {

    // ----------------------------
    // 1️⃣ Test real behavior for fromId
    // ----------------------------
    @Test
    void testFromIdValid() {
        assertEquals(TransactionTypeEnum.INCOME, TransactionTypeEnum.fromId(1));
        assertEquals(TransactionTypeEnum.SAVINGS, TransactionTypeEnum.fromId(4));
        assertEquals(TransactionTypeEnum.CHARITY, TransactionTypeEnum.fromId(19));
    }

    @Test
    void testFromIdInvalid() {
        Exception ex = assertThrows(IllegalArgumentException.class,
                () -> TransactionTypeEnum.fromId(99));
        assertTrue(ex.getMessage().contains("Invalid TransactionType ID"));
    }

    // ----------------------------
    // 2️⃣ Test real behavior for fromDisplayName
    // ----------------------------
    @Test
    void testFromDisplayNameValid() {
        assertEquals(TransactionTypeEnum.INCOME, TransactionTypeEnum.fromDisplayName("Income"));
        assertEquals(TransactionTypeEnum.HEALTH, TransactionTypeEnum.fromDisplayName("health")); // case-insensitive
        assertEquals(TransactionTypeEnum.CAR, TransactionTypeEnum.fromDisplayName("Car"));
    }

    @Test
    void testFromDisplayNameInvalid() {
        Exception ex = assertThrows(IllegalArgumentException.class,
                () -> TransactionTypeEnum.fromDisplayName("Unknown"));
        assertTrue(ex.getMessage().contains("Invalid TransactionType name"));
    }

    // ----------------------------
    // 3️⃣ Test getters
    // ----------------------------
    @Test
    void testGetIdAndDisplayName() {
        TransactionTypeEnum t = TransactionTypeEnum.INCOME_TAX;
        assertEquals(2, t.getId());
        assertEquals("Income Tax", t.getDisplayName());
    }

    // ----------------------------
    // 4️⃣ Optional: Mockito static mock
    // ----------------------------
    @Test
    void testFromIdWithMockito() {
        try (MockedStatic<TransactionTypeEnum> mocked = mockStatic(TransactionTypeEnum.class)) {
            mocked.when(() -> TransactionTypeEnum.fromId(1))
                    .thenReturn(TransactionTypeEnum.INCOME);

            TransactionTypeEnum result = TransactionTypeEnum.fromId(1);
            assertEquals(TransactionTypeEnum.INCOME, result);

            mocked.verify(() -> TransactionTypeEnum.fromId(1));
        }
    }

    @Test
    void testFromDisplayNameWithMockito() {
        try (MockedStatic<TransactionTypeEnum> mocked = mockStatic(TransactionTypeEnum.class)) {
            mocked.when(() -> TransactionTypeEnum.fromDisplayName("Savings"))
                    .thenReturn(TransactionTypeEnum.SAVINGS);

            TransactionTypeEnum result = TransactionTypeEnum.fromDisplayName("Savings");
            assertEquals(TransactionTypeEnum.SAVINGS, result);

            mocked.verify(() -> TransactionTypeEnum.fromDisplayName("Savings"));
        }
    }
}