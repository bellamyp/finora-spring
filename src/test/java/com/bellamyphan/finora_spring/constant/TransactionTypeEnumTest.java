package com.bellamyphan.finora_spring.constant;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mockStatic;

@ExtendWith(MockitoExtension.class)
class TransactionTypeEnumTest {

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
}