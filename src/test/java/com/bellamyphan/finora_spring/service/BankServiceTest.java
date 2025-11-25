package com.bellamyphan.finora_spring.service;

import com.bellamyphan.finora_spring.entity.Bank;
import com.bellamyphan.finora_spring.entity.User;
import com.bellamyphan.finora_spring.repository.BankRepository;
import com.bellamyphan.finora_spring.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;

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

    @Test
    void createBank_successful() {
        Bank bank = new Bank();
        bank.setName("Test Bank");

        when(nanoIdService.generate()).thenReturn("ABC123DEF4");
        when(bankRepository.save(any(Bank.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Bank savedBank = bankService.createBank(bank);

        assertEquals("ABC123DEF4", savedBank.getId());
        assertEquals("Test Bank", savedBank.getName());
        verify(bankRepository, times(1)).save(bank);
    }

    @Test
    void createBank_retryOnCollision() {
        Bank bank = new Bank();
        bank.setName("Retry Bank");

        // First 2 attempts throw collision
        when(nanoIdService.generate())
                .thenReturn("COLLISION1")
                .thenReturn("COLLISION2")
                .thenReturn("OK12345678");

        when(bankRepository.save(any(Bank.class)))
                .thenThrow(DataIntegrityViolationException.class)
                .thenThrow(DataIntegrityViolationException.class)
                .thenAnswer(invocation -> invocation.getArgument(0));

        Bank savedBank = bankService.createBank(bank);

        assertEquals("OK12345678", savedBank.getId());
        verify(bankRepository, times(3)).save(bank);
    }

    @Test
    void createBank_failAfter10Attempts() {
        Bank bank = new Bank();
        when(nanoIdService.generate()).thenReturn("DUPLICATE");

        when(bankRepository.save(any(Bank.class))).thenThrow(DataIntegrityViolationException.class);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> bankService.createBank(bank));
        assertEquals("Failed to generate unique Bank ID after 10 attempts", ex.getMessage());
        verify(bankRepository, times(10)).save(bank);
    }

    @Test
    void findBanksByUser_success() {
        User user = new User();
        user.setId("user1");

        Bank bank1 = new Bank();
        bank1.setName("Bank1");

        Bank bank2 = new Bank();
        bank2.setName("Bank2");

        when(bankRepository.findByUser(user)).thenReturn(List.of(bank1, bank2));

        List<Bank> banks = bankService.findBanksByUser(user);
        assertEquals(2, banks.size());
        assertEquals("Bank1", banks.get(0).getName());
    }

    @Test
    void findBanksByUser_nullUser() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> bankService.findBanksByUser(null));
        assertEquals("User cannot be null", ex.getMessage());
    }

    @Test
    void findBankById_success() {
        Bank bank = new Bank();
        bank.setId("bank123");

        when(bankRepository.findById("bank123")).thenReturn(Optional.of(bank));

        Bank found = bankService.findBankById("bank123");
        assertEquals(bank, found);
    }

    @Test
    void findBankById_notFound() {
        when(bankRepository.findById("missing")).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> bankService.findBankById("missing"));
        assertEquals("Bank not found: missing", ex.getMessage());
    }

    @Test
    void calculateBalance_success() {
        when(transactionRepository.calculateBankBalance("bank123")).thenReturn(BigDecimal.valueOf(1234.56));

        BigDecimal balance = bankService.calculateBalance("bank123");
        assertEquals(BigDecimal.valueOf(1234.56), balance);
    }
}
