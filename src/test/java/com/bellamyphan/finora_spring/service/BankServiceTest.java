package com.bellamyphan.finora_spring.service;

import com.bellamyphan.finora_spring.entity.Bank;
import com.bellamyphan.finora_spring.entity.User;
import com.bellamyphan.finora_spring.repository.BankRepository;
import com.bellamyphan.finora_spring.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BankServiceTest {

    private BankRepository bankRepository;
    private TransactionRepository transactionRepository;
    private NanoIdService nanoIdService;
    private BankService bankService;

    @BeforeEach
    void setUp() {
        bankRepository = mock(BankRepository.class);
        transactionRepository = mock(TransactionRepository.class);
        nanoIdService = mock(NanoIdService.class);
        bankService = new BankService(bankRepository, transactionRepository, nanoIdService);
    }

    // -------------------------------------------------------
    // CREATE BANK
    // -------------------------------------------------------
    @Test
    void createBank_successful() {
        Bank bank = new Bank();
        bank.setName("Test Bank");

        when(nanoIdService.generateUniqueId(bankRepository))
                .thenReturn("ABC123DEF4");

        when(bankRepository.save(any(Bank.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Bank result = bankService.createBank(bank);

        assertEquals("ABC123DEF4", result.getId());
        assertEquals("Test Bank", result.getName());
        verify(bankRepository).save(bank);
    }

    // -------------------------------------------------------
    // FIND BANKS BY USER
    // -------------------------------------------------------
    @Test
    void findBanksByUser_success() {
        User user = new User();
        user.setId("user1");

        Bank b1 = new Bank(); b1.setName("Bank One");
        Bank b2 = new Bank(); b2.setName("Bank Two");

        when(bankRepository.findByUser(user))
                .thenReturn(List.of(b1, b2));

        List<Bank> result = bankService.findBanksByUser(user);

        assertEquals(2, result.size());
        assertEquals("Bank One", result.get(0).getName());
        assertEquals("Bank Two", result.get(1).getName());
    }

    @Test
    void findBanksByUser_nullUser_throwsException() {
        IllegalArgumentException ex =
                assertThrows(IllegalArgumentException.class,
                        () -> bankService.findBanksByUser(null));

        assertEquals("User cannot be null", ex.getMessage());
    }

    // -------------------------------------------------------
    // FIND BY ID
    // -------------------------------------------------------
    @Test
    void findBankById_success() {
        Bank bank = new Bank();
        bank.setId("bank123");

        when(bankRepository.findById("bank123"))
                .thenReturn(Optional.of(bank));

        Bank found = bankService.findBankById("bank123");

        assertEquals(bank, found);
    }

    @Test
    void findBankById_notFound_throwsException() {
        when(bankRepository.findById("missing"))
                .thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> bankService.findBankById("missing"));

        assertEquals("Bank not found: missing", ex.getMessage());
    }

    // -------------------------------------------------------
    // CALCULATE BALANCE
    // -------------------------------------------------------
    @Test
    void calculateBalance_success() {
        when(transactionRepository.calculateBankBalance("bank123"))
                .thenReturn(BigDecimal.valueOf(999.99));

        BigDecimal balance = bankService.calculateBalance("bank123");

        assertEquals(BigDecimal.valueOf(999.99), balance);
    }
}
