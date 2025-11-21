package com.bellamyphan.finora_spring.entity;

import com.bellamyphan.finora_spring.constant.BankTypeEnum;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class BankTest {

    @Test
    void testNoArgsConstructorAndSetters() {
        Bank bank = new Bank();

        // Create related entities
        User user = new User();
        user.setId("user123");
        user.setEmail("test@example.com");

        BankType type = new BankType(BankTypeEnum.SAVINGS);

        // Set values
        bank.setId("bank123456");
        bank.setName("Test Bank");
        bank.setOpeningDate(LocalDate.of(2023, 1, 1));
        bank.setClosingDate(LocalDate.of(2025, 12, 31));
        bank.setType(type);
        bank.setUser(user);

        // Assertions
        assertEquals("bank123456", bank.getId());
        assertEquals("Test Bank", bank.getName());
        assertEquals(LocalDate.of(2023, 1, 1), bank.getOpeningDate());
        assertEquals(LocalDate.of(2025, 12, 31), bank.getClosingDate());
        assertEquals(type, bank.getType());
        assertEquals(user, bank.getUser());
    }

    @Test
    void testConstructorWithoutId() {
        User user = new User();
        user.setId("user456");
        user.setEmail("user2@example.com");

        BankType type = new BankType(BankTypeEnum.CHECKING);

        Bank bank = new Bank(
                "My Bank",
                LocalDate.of(2022, 5, 15),
                null, // closingDate null
                type,
                user
        );

        // Assertions
        assertNull(bank.getId(), "ID should be null for constructor without id");
        assertEquals("My Bank", bank.getName());
        assertEquals(LocalDate.of(2022, 5, 15), bank.getOpeningDate());
        assertNull(bank.getClosingDate());
        assertEquals(type, bank.getType());
        assertEquals(user, bank.getUser());
    }

    @Test
    void testAllArgsConstructorWithId() {
        User user = new User();
        user.setId("user789");
        user.setEmail("user3@example.com");

        BankType type = new BankType(BankTypeEnum.CHECKING);

        Bank bank = new Bank(
                "bank123456", // id included
                "Bank Constructor",
                LocalDate.of(2022, 5, 15),
                null, // closingDate null
                type,
                user
        );

        // Assertions
        assertEquals("bank123456", bank.getId(), "ID should be set by all-args constructor");
        assertEquals("Bank Constructor", bank.getName());
        assertEquals(LocalDate.of(2022, 5, 15), bank.getOpeningDate());
        assertNull(bank.getClosingDate());
        assertEquals(type, bank.getType());
        assertEquals(user, bank.getUser());
    }
}