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

        // related entities
        User user = new User();
        user.setId("user123");
        user.setEmail("test@example.com");

        BankType type = new BankType(BankTypeEnum.SAVINGS);

        BankGroup group = new BankGroup();
        group.setId("grp1");
        group.setName("Main Group");

        // Set values
        bank.setId("bank123456");
        bank.setName("Test Bank");
        bank.setOpeningDate(LocalDate.of(2023, 1, 1));
        bank.setClosingDate(LocalDate.of(2025, 12, 31));
        bank.setGroup(group);
        bank.setType(type);
        bank.setUser(user);

        // Assertions
        assertEquals("bank123456", bank.getId());
        assertEquals("Test Bank", bank.getName());
        assertEquals(LocalDate.of(2023, 1, 1), bank.getOpeningDate());
        assertEquals(LocalDate.of(2025, 12, 31), bank.getClosingDate());
        assertEquals(group, bank.getGroup());
        assertEquals(type, bank.getType());
        assertEquals(user, bank.getUser());
    }

    @Test
    void testAllArgsConstructor() {
        User user = new User();
        user.setId("user456");
        user.setEmail("user2@example.com");

        BankType type = new BankType(BankTypeEnum.CHECKING);

        BankGroup group = new BankGroup();
        group.setId("grp2");
        group.setName("Secondary Group");

        Bank bank = new Bank(
                "bank001",
                "My Bank",
                LocalDate.of(2022, 5, 15),
                null,         // closingDate null
                group,
                type,
                user
        );

        // Assertions
        assertEquals("bank001", bank.getId());
        assertEquals("My Bank", bank.getName());
        assertEquals(LocalDate.of(2022, 5, 15), bank.getOpeningDate());
        assertNull(bank.getClosingDate());
        assertEquals(group, bank.getGroup());
        assertEquals(type, bank.getType());
        assertEquals(user, bank.getUser());
    }
}
