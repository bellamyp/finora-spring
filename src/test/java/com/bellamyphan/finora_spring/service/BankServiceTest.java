package com.bellamyphan.finora_spring.service;

import com.bellamyphan.finora_spring.constant.BankTypeEnum;
import com.bellamyphan.finora_spring.dto.BankCreateDto;
import com.bellamyphan.finora_spring.dto.BankDto;
import com.bellamyphan.finora_spring.entity.Bank;
import com.bellamyphan.finora_spring.entity.BankGroup;
import com.bellamyphan.finora_spring.entity.BankType;
import com.bellamyphan.finora_spring.entity.User;
import com.bellamyphan.finora_spring.repository.BankGroupRepository;
import com.bellamyphan.finora_spring.repository.BankRepository;
import com.bellamyphan.finora_spring.repository.BankTypeRepository;
import com.bellamyphan.finora_spring.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BankServiceTest {

    private BankRepository bankRepository;
    private BankGroupRepository bankGroupRepository;
    private BankTypeRepository bankTypeRepository;
    private TransactionRepository transactionRepository;
    private NanoIdService nanoIdService;

    private BankService bankService;

    @BeforeEach
    void setUp() {
        bankRepository = mock(BankRepository.class);
        bankGroupRepository = mock(BankGroupRepository.class);
        bankTypeRepository = mock(BankTypeRepository.class);
        transactionRepository = mock(TransactionRepository.class);
        nanoIdService = mock(NanoIdService.class);

        bankService = new BankService(
                bankRepository,
                bankGroupRepository,
                bankTypeRepository,
                transactionRepository,
                nanoIdService
        );
    }

    // -------------------------------------------------------
    // CREATE BANK
    // -------------------------------------------------------
    @Test
    void createBank_success() {
        // Input
        BankCreateDto dto = new BankCreateDto();
        dto.setName("My Bank");
        dto.setGroupId("group1");
        dto.setType(BankTypeEnum.CHECKING);
        dto.setOpeningDate(LocalDate.of(2024, 1, 1));

        User user = new User();
        user.setId("u1");
        user.setEmail("user@example.com");

        // Mock: ID generation
        when(nanoIdService.generateUniqueId(bankRepository))
                .thenReturn("BANK12345");

        // Mock: BankGroup lookup
        BankGroup group = new BankGroup();
        group.setId("group1");

        when(bankGroupRepository.findById("group1"))
                .thenReturn(Optional.of(group));

        // Mock: BankType lookup
        BankType type = new BankType(BankTypeEnum.CHECKING);
        when(bankTypeRepository.findByType(BankTypeEnum.CHECKING))
                .thenReturn(Optional.of(type));

        // Mock: Save
        when(bankRepository.save(any(Bank.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Execute
        BankDto result = bankService.createBank(dto, user);

        // Verify output
        assertEquals("BANK12345", result.getId());
        assertEquals("My Bank", result.getName());
        assertEquals(BankTypeEnum.CHECKING, result.getType());
        assertEquals("user@example.com", result.getEmail());

        // Verify save was called
        verify(bankRepository).save(any(Bank.class));
    }

    @Test
    void createBank_groupNotFound_throws() {
        BankCreateDto dto = new BankCreateDto();
        dto.setGroupId("missing");
        dto.setType(BankTypeEnum.CHECKING);

        when(bankGroupRepository.findById("missing"))
                .thenReturn(Optional.empty());

        User user = new User();

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> bankService.createBank(dto, user));

        assertEquals("Bank group id is not found: missing", ex.getMessage());
    }

    @Test
    void createBank_typeNotFound_throws() {
        BankCreateDto dto = new BankCreateDto();
        dto.setGroupId("group1");
        dto.setType(BankTypeEnum.CHECKING);

        BankGroup group = new BankGroup();
        group.setId("group1");

        when(bankGroupRepository.findById("group1"))
                .thenReturn(Optional.of(group));

        when(bankTypeRepository.findByType(BankTypeEnum.CHECKING))
                .thenReturn(Optional.empty());

        User user = new User();

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> bankService.createBank(dto, user));

        assertEquals("Bank type is not found: CHECKING", ex.getMessage());
    }

    // -------------------------------------------------------
    // FIND BANKS BY USER
    // -------------------------------------------------------
    @Test
    void findBanksByUser_success() {
        User user = new User();
        user.setId("user1");

        Bank b1 = new Bank();
        b1.setName("Bank A");
        Bank b2 = new Bank();
        b2.setName("Bank B");

        when(bankRepository.findByUser(user))
                .thenReturn(List.of(b1, b2));

        List<Bank> result = bankService.findBanksByUser(user);

        assertEquals(2, result.size());
        assertEquals("Bank A", result.get(0).getName());
        assertEquals("Bank B", result.get(1).getName());
    }

    @Test
    void findBanksByUser_nullUser_throws() {
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

        assertEquals("bank123", found.getId());
    }

    @Test
    void findBankById_notFound_throws() {
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
                .thenReturn(BigDecimal.valueOf(455.75));

        BigDecimal balance = bankService.calculateBalance("bank123");

        assertEquals(BigDecimal.valueOf(455.75), balance);
    }
}
