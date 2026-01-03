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
        BankCreateDto dto = new BankCreateDto();
        dto.setName("My Bank");
        dto.setGroupId("group1");
        dto.setType(BankTypeEnum.CHECKING);
        dto.setOpeningDate(LocalDate.of(2024, 1, 1));

        User user = new User();
        user.setId("u1");
        user.setEmail("user@example.com");

        when(nanoIdService.generateUniqueId(bankRepository))
                .thenReturn("BANK12345");

        BankGroup group = new BankGroup();
        group.setId("group1");
        when(bankGroupRepository.findById("group1")).thenReturn(Optional.of(group));

        BankType type = new BankType(BankTypeEnum.CHECKING);
        when(bankTypeRepository.findByType(BankTypeEnum.CHECKING))
                .thenReturn(Optional.of(type));

        when(bankRepository.save(any(Bank.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        BankDto result = bankService.createBank(dto, user);

        assertEquals("BANK12345", result.getId());
        assertEquals("My Bank", result.getName());
        assertEquals(BankTypeEnum.CHECKING, result.getType());
        assertEquals("user@example.com", result.getEmail());

        verify(bankRepository).save(any(Bank.class));
    }

    @Test
    void createBank_groupNotFound_throws() {
        BankCreateDto dto = new BankCreateDto();
        dto.setGroupId("missing");
        dto.setType(BankTypeEnum.CHECKING);

        when(bankGroupRepository.findById("missing")).thenReturn(Optional.empty());

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
        when(bankGroupRepository.findById("group1")).thenReturn(Optional.of(group));
        when(bankTypeRepository.findByType(BankTypeEnum.CHECKING)).thenReturn(Optional.empty());

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

        BankGroup group1 = new BankGroup();
        group1.setId("g1");
        group1.setName("Group A");

        BankGroup group2 = new BankGroup();
        group2.setId("g2");
        group2.setName("Group B");

        BankType type = new BankType(BankTypeEnum.CHECKING);

        Bank b1 = new Bank("b1", "Bank 1", null, null, group2, type, user);
        Bank b2 = new Bank("b2", "Bank 2", null, null, group1, type, user);

        when(bankRepository.findByUser(user)).thenReturn(List.of(b1, b2));

        // Mock both pending and posted balances
        when(transactionRepository.calculatePendingBankBalance("b1")).thenReturn(BigDecimal.valueOf(100));
        when(transactionRepository.calculatePostedBankBalance("b1")).thenReturn(BigDecimal.valueOf(500));

        when(transactionRepository.calculatePendingBankBalance("b2")).thenReturn(BigDecimal.valueOf(200));
        when(transactionRepository.calculatePostedBankBalance("b2")).thenReturn(BigDecimal.valueOf(600));

        List<BankDto> result = bankService.findBanksByUser(user);

        // Should be sorted by group name: Group A, Group B
        assertEquals(2, result.size());

        BankDto first = result.get(0); // Bank 2 -> Group A
        BankDto second = result.get(1); // Bank 1 -> Group B

        assertEquals("b2", first.getId());
        assertEquals(BigDecimal.valueOf(200), first.getPendingBalance());
        assertEquals(BigDecimal.valueOf(600), first.getPostedBalance());

        assertEquals("b1", second.getId());
        assertEquals(BigDecimal.valueOf(100), second.getPendingBalance());
        assertEquals(BigDecimal.valueOf(500), second.getPostedBalance());
    }

    @Test
    void findBanksByUser_nullUser_throws() {
        IllegalArgumentException ex =
                assertThrows(IllegalArgumentException.class,
                        () -> bankService.findBanksByUser(null));
        assertEquals("User cannot be null", ex.getMessage());
    }

    // -------------------------------------------------------
    // FIND BANK BY ID
    // -------------------------------------------------------
    @Test
    void findBankById_success() {
        Bank bank = new Bank();
        bank.setId("bank123");

        when(bankRepository.findById("bank123")).thenReturn(Optional.of(bank));

        Bank found = bankService.findBankById("bank123");

        assertEquals("bank123", found.getId());
    }

    @Test
    void findBankById_notFound_throws() {
        when(bankRepository.findById("missing")).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> bankService.findBankById("missing"));

        assertEquals("Bank not found: missing", ex.getMessage());
    }

    // -------------------------------------------------------
    // CALCULATE BALANCE
    // -------------------------------------------------------
    @Test
    void calculatePendingBalance_success() {
        when(transactionRepository.calculatePendingBankBalance("bank123"))
                .thenReturn(BigDecimal.valueOf(455.75));

        BigDecimal balance = bankService.calculatePendingBalance("bank123");

        assertEquals(BigDecimal.valueOf(455.75), balance);
    }
}
