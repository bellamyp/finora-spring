package com.bellamyphan.finora_spring.entity;

import com.bellamyphan.finora_spring.constant.TransactionTypeEnum;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class TransactionTest {

    // Helper to create dummy related entities
    private TransactionGroup createGroup() {
        TransactionGroup group = new TransactionGroup();
        group.setId("group123");
        group.setReport(null); // optional, can provide a Report if needed
        return group;
    }

    private Bank createBank() {
        Bank bank = new Bank();
        bank.setId("bank123");
        bank.setName("Test Bank");
        return bank;
    }

    private Brand createBrand() {
        Brand brand = new Brand();
        brand.setId("brand123");
        brand.setName("Test Brand");
        return brand;
    }

    private TransactionType createType() {
        TransactionType type = new TransactionType();
        type.setId("type123");
        type.setType(TransactionTypeEnum.INCOME);
        return type;
    }

    @Test
    void testNoArgsConstructorAndSetters() {
        Transaction transaction = new Transaction();

        transaction.setId("txn123");
        transaction.setGroup(createGroup());
        transaction.setBank(createBank());
        transaction.setBrand(createBrand());
        transaction.setType(createType());
        transaction.setDate(LocalDate.of(2025, 11, 21));
        transaction.setAmount(BigDecimal.valueOf(123.45));
        transaction.setNotes("Test notes");

        assertEquals("txn123", transaction.getId());
        assertEquals("Test Bank", transaction.getBank().getName());
        assertEquals("Test Brand", transaction.getBrand().getName());
        assertEquals(TransactionTypeEnum.INCOME, transaction.getType().getType());
        assertEquals(LocalDate.of(2025, 11, 21), transaction.getDate());
        assertEquals(BigDecimal.valueOf(123.45), transaction.getAmount());
        assertEquals("Test notes", transaction.getNotes());
    }

    @Test
    void testNotNullFields() {
        Transaction transaction = new Transaction();

        transaction.setGroup(createGroup());
        transaction.setBank(createBank());
        transaction.setBrand(createBrand());
        transaction.setType(createType());
        transaction.setDate(LocalDate.now());
        transaction.setAmount(BigDecimal.ONE);

        assertNotNull(transaction.getGroup());
        assertNotNull(transaction.getBank());
        assertNotNull(transaction.getBrand());
        assertNotNull(transaction.getType());
        assertNotNull(transaction.getDate());
        assertNotNull(transaction.getAmount());
    }
}
