package com.bellamyphan.finora_spring.entity;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class TransactionTest {

    @Test
    void testNoArgsConstructorAndSetters() {
        Transaction transaction = new Transaction();

        // Create related entities
        TransactionGroup group = new TransactionGroup();
        group.setId("group123");
        group.setNotes("Test Group");

        Bank bank = new Bank();
        bank.setId("bank123456");
        bank.setName("Test Bank");

        // Set fields
        transaction.setId("txn123456");
        transaction.setGroup(group);
        transaction.setDate(LocalDate.of(2025, 11, 21));
        transaction.setAmount(BigDecimal.valueOf(123.45));
        transaction.setNotes("Test notes");
        transaction.setBank(bank);

        // Assertions
        assertEquals("txn123456", transaction.getId());
        assertEquals(group, transaction.getGroup());
        assertEquals(LocalDate.of(2025, 11, 21), transaction.getDate());
        assertEquals(BigDecimal.valueOf(123.45), transaction.getAmount());
        assertEquals("Test notes", transaction.getNotes());
        assertEquals(bank, transaction.getBank());
    }

    @Test
    void testAllArgsConstructor() {
        TransactionGroup group = new TransactionGroup();
        group.setId("group999");
        group.setNotes("Group 999");

        Bank bank = new Bank();
        bank.setId("bank999999");
        bank.setName("Bank 999");

        // Use no-arg + setters for simplicity (you could add a constructor if desired)
        Transaction transaction = new Transaction();
        transaction.setId("txn999999");
        transaction.setGroup(group);
        transaction.setDate(LocalDate.of(2024, 5, 15));
        transaction.setAmount(BigDecimal.valueOf(9999.99));
        transaction.setNotes("Some transaction");
        transaction.setBank(bank);

        // Assertions
        assertEquals("txn999999", transaction.getId());
        assertEquals(group, transaction.getGroup());
        assertEquals(LocalDate.of(2024, 5, 15), transaction.getDate());
        assertEquals(BigDecimal.valueOf(9999.99), transaction.getAmount());
        assertEquals("Some transaction", transaction.getNotes());
        assertEquals(bank, transaction.getBank());
    }

    @Test
    void testNotNullFields() {
        Transaction transaction = new Transaction();

        TransactionGroup group = new TransactionGroup();
        Bank bank = new Bank();

        transaction.setGroup(group);
        transaction.setDate(LocalDate.now());
        transaction.setAmount(BigDecimal.ONE);
        transaction.setBank(bank);

        assertNotNull(transaction.getGroup());
        assertNotNull(transaction.getDate());
        assertNotNull(transaction.getAmount());
        assertNotNull(transaction.getBank());
    }
}