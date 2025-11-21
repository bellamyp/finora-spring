package com.bellamyphan.finora_spring.entity;

import com.bellamyphan.finora_spring.constant.BankTypeEnum;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class BankTypeTest {

    @Test
    void testNoArgsConstructorAndSetters() {
        BankType bankType = new BankType();

        // Set values
        bankType.setId("banktype123");
        bankType.setType(BankTypeEnum.SAVINGS);

        // Assertions
        assertEquals("banktype123", bankType.getId());
        assertEquals(BankTypeEnum.SAVINGS, bankType.getType());
    }

    @Test
    void testEnumConstructor() {
        BankType bankType = new BankType(BankTypeEnum.CHECKING);

        // Assertions
        assertNull(bankType.getId(), "ID should be null if not set");
        assertEquals(BankTypeEnum.CHECKING, bankType.getType());
    }

    @Test
    void testSettersAndGetters() {
        BankType bankType = new BankType();
        bankType.setId("type999");
        bankType.setType(BankTypeEnum.REWARDS);

        assertEquals("type999", bankType.getId());
        assertEquals(BankTypeEnum.REWARDS, bankType.getType());
    }
}