package com.bellamyphan.finora_spring.constant;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mockStatic;

@ExtendWith(MockitoExtension.class)
class BankTypeEnumTest {

    // ----------------------------
    // 1️⃣ Test static method with Mockito mock
    // ----------------------------
    @Test
    void testFromDisplayNameWithMockito() {
        try (MockedStatic<BankTypeEnum> mocked = mockStatic(BankTypeEnum.class)) {
            // Mock fromDisplayName to return CHECKING when input is "Checking"
            mocked.when(() -> BankTypeEnum.fromDisplayName("Checking"))
                    .thenReturn(BankTypeEnum.CHECKING);

            BankTypeEnum result = BankTypeEnum.fromDisplayName("Checking");

            assertEquals(BankTypeEnum.CHECKING, result);

            // Verify that the static method was called with the expected argument
            mocked.verify(() -> BankTypeEnum.fromDisplayName("Checking"));
        }
    }

    // ----------------------------
    // 2️⃣ Test real behavior (without mock)
    // ----------------------------
    @Test
    void testFromDisplayNameRealBehavior() {
        assertEquals(BankTypeEnum.CHECKING, BankTypeEnum.fromDisplayName("checking"));
        assertEquals(BankTypeEnum.SAVINGS, BankTypeEnum.fromDisplayName("SAVINGS"));

        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> BankTypeEnum.fromDisplayName("Unknown"));
        assertTrue(exception.getMessage().contains("No matching BankTypeEnum for display name"));
    }

    // ----------------------------
    // 3️⃣ Test enum instance methods
    // ----------------------------
    @Test
    void testGetEnumNameAndDisplayName() {
        BankTypeEnum checking = BankTypeEnum.CHECKING;
        assertEquals("CHECKING", checking.getEnumName());
        assertEquals("Checking", checking.getDisplayName());

        BankTypeEnum savings = BankTypeEnum.SAVINGS;
        assertEquals("SAVINGS", savings.getEnumName());
        assertEquals("Savings", savings.getDisplayName());
    }
}