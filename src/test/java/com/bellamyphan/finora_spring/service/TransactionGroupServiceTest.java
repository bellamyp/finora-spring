package com.bellamyphan.finora_spring.service;

import com.bellamyphan.finora_spring.constant.TransactionTypeEnum;
import com.bellamyphan.finora_spring.dto.TransactionCreateDto;
import com.bellamyphan.finora_spring.dto.TransactionGroupCreateDto;
import com.bellamyphan.finora_spring.entity.*;
import com.bellamyphan.finora_spring.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class TransactionGroupServiceTest {

    @Mock NanoIdService nanoIdService;
    @Mock TransactionGroupRepository transactionGroupRepository;
    @Mock TransactionRepository transactionRepository;
    @Mock BrandRepository brandRepository;
    @Mock TransactionTypeRepository transactionTypeRepository;
    @Mock BankRepository bankRepository;

    @InjectMocks TransactionGroupService service;

    // ------------------------------------------------------------------------
    //   BASIC SUCCESS TEST (LEAN)
    // ------------------------------------------------------------------------
    @Test
    void createTransactionGroup_success() {
        // Arrange ---------------------------------------------------------------
        Brand brand = new Brand(); brand.setId("brand1");
        TransactionType type = new TransactionType(); type.setType(TransactionTypeEnum.SHOP);
        Bank bank = new Bank(); bank.setId("bank1");

        when(brandRepository.findById("brand1")).thenReturn(Optional.of(brand));
        when(transactionTypeRepository.findByType(TransactionTypeEnum.SHOP)).thenReturn(Optional.of(type));
        when(bankRepository.findById("bank1")).thenReturn(Optional.of(bank));

        // nano IDs
        when(nanoIdService.generate()).thenReturn("G1", "T1");

        // save group
        TransactionGroup savedGroup = new TransactionGroup();
        savedGroup.setId("G1");
        when(transactionGroupRepository.save(any())).thenReturn(savedGroup);

        // save transaction
        when(transactionRepository.save(any())).thenAnswer(inv -> {
            Transaction t = inv.getArgument(0);
            t.setId("T1");
            return t;
        });

        TransactionCreateDto row = new TransactionCreateDto();
        row.setAmount(30.0);
        row.setBankId("bank1");
        row.setNotes("Lunch");

        TransactionGroupCreateDto dto = new TransactionGroupCreateDto();
        dto.setBrandId("brand1");
        dto.setTypeId("SHOP");
        dto.setDate("2024-12-01");
        dto.setTransactions(List.of(row));

        // Act -------------------------------------------------------------------
        String result = service.createTransactionGroup(dto);

        // Assert ----------------------------------------------------------------
        assertEquals("G1", result);

        ArgumentCaptor<Transaction> txCap = ArgumentCaptor.forClass(Transaction.class);
        verify(transactionRepository).save(txCap.capture());
        Transaction savedTx = txCap.getValue();

        assertEquals(new BigDecimal("30.0"), savedTx.getAmount());
        assertEquals(LocalDate.parse("2024-12-01"), savedTx.getDate());
        assertEquals("Lunch", savedTx.getNotes());
        assertEquals(bank, savedTx.getBank());
        assertEquals(brand, savedTx.getBrand());
        assertEquals(type, savedTx.getType());
        assertEquals(savedGroup, savedTx.getGroup());
    }

    // ------------------------------------------------------------------------
    //   RETRY ON GROUP ID COLLISION
    // ------------------------------------------------------------------------
    @Test
    void createTransactionGroup_groupIdRetriesOnCollision() {
        // Arrange ---------------------------------------------------------------
        when(brandRepository.findById(any())).thenReturn(Optional.of(new Brand()));
        when(transactionTypeRepository.findByType(any())).thenReturn(Optional.of(new TransactionType()));
        when(bankRepository.findById(any())).thenReturn(Optional.of(new Bank()));

        when(nanoIdService.generate()).thenReturn("BAD", "GOOD");

        when(transactionGroupRepository.save(any()))
                .thenThrow(new DataIntegrityViolationException("dup"))
                .thenAnswer(i -> {
                    TransactionGroup g = i.getArgument(0);
                    g.setId("GOOD");
                    return g;
                });

        when(transactionRepository.save(any())).thenReturn(new Transaction());

        TransactionCreateDto row = new TransactionCreateDto();
        row.setBankId("bank1");

        TransactionGroupCreateDto dto = new TransactionGroupCreateDto();
        dto.setBrandId("brand1");
        dto.setTypeId("SHOP");
        dto.setDate("2024-01-01");
        dto.setTransactions(List.of(row));

        // Act -------------------------------------------------------------------
        String id = service.createTransactionGroup(dto);

        // Assert ----------------------------------------------------------------
        assertEquals("GOOD", id);
        verify(nanoIdService, times(3)).generate();
    }
}