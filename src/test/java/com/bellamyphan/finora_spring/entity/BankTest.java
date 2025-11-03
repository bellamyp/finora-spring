package com.bellamyphan.finora_spring.entity;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class BankTest {

    @Test
    void testNoArgsConstructorAndSetters() {
        Bank bank = new Bank();

        // Create related entities
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail("test@example.com");

        BankType type = new BankType("SAVINGS");

        // Set values
        bank.setId(UUID.randomUUID());
        bank.setName("Test Bank");
        bank.setOpeningDate(LocalDate.of(2023, 1, 1));
        bank.setClosingDate(LocalDate.of(2025, 12, 31));
        bank.setType(type);
        bank.setUser(user);

        // Assertions
        assertNotNull(bank.getId());
        assertEquals("Test Bank", bank.getName());
        assertEquals(LocalDate.of(2023, 1, 1), bank.getOpeningDate());
        assertEquals(LocalDate.of(2025, 12, 31), bank.getClosingDate());
        assertEquals(type, bank.getType());
        assertEquals(user, bank.getUser());
    }

    @Test
    void testAllArgsConstructor() {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail("user2@example.com");

        BankType type = new BankType("CHECKING");

        Bank bank = new Bank(
                "Bank Constructor",
                LocalDate.of(2022, 5, 15),
                null, // closingDate null
                type,
                user
        );

        // Assertions
        assertEquals("Bank Constructor", bank.getName());
        assertEquals(LocalDate.of(2022, 5, 15), bank.getOpeningDate());
        assertNull(bank.getClosingDate());
        assertEquals(type, bank.getType());
        assertEquals(user, bank.getUser());
    }
}
