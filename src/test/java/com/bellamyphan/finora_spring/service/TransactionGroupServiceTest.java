package com.bellamyphan.finora_spring.service;

import com.bellamyphan.finora_spring.dto.TransactionGroupResponseDto;
import com.bellamyphan.finora_spring.entity.*;
import com.bellamyphan.finora_spring.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionGroupServiceTest {

    @Mock NanoIdService nanoIdService;
    @Mock TransactionGroupRepository transactionGroupRepository;
    @Mock TransactionRepository transactionRepository;
    @Mock PendingTransactionRepository pendingTransactionRepository;
    @Mock BrandRepository brandRepository;
    @Mock TransactionTypeRepository transactionTypeRepository;
    @Mock BankRepository bankRepository;

    @InjectMocks TransactionGroupService service;

    @Test
    void getPendingTransactionGroupsForUser_filtersCorrectly() {
        // Arrange
        User user = new User();
        user.setId("user1");

        TransactionGroup group1 = new TransactionGroup();
        group1.setId("G1");
        TransactionGroup group2 = new TransactionGroup();
        group2.setId("G2");

        Brand brand = new Brand();
        brand.setId("brand1");

        TransactionType type = new TransactionType();
        type.setId("SHOP");

        Bank bank1 = new Bank();
        bank1.setUser(user);
        Bank bank2 = new Bank();
        bank2.setUser(user);

        Transaction tx1 = new Transaction();
        tx1.setId("T1");
        tx1.setBank(bank1);
        tx1.setBrand(brand);
        tx1.setType(type);
        tx1.setDate(LocalDate.of(2024, 1, 1));

        Transaction tx2 = new Transaction();
        tx2.setId("T2");
        tx2.setBank(bank2);
        tx2.setBrand(brand);
        tx2.setType(type);
        tx2.setDate(LocalDate.of(2024, 1, 2));

        when(transactionGroupRepository.findAll()).thenReturn(List.of(group1, group2));
        when(transactionRepository.findByGroup(group1)).thenReturn(List.of(tx1));
        when(transactionRepository.findByGroup(group2)).thenReturn(List.of(tx2));
        when(pendingTransactionRepository.existsByTransactionId("T1")).thenReturn(true);
        when(pendingTransactionRepository.existsByTransactionId("T2")).thenReturn(false);

        // Act
        List<TransactionGroupResponseDto> result = service.getPendingTransactionGroupsForUser(user);

        // Assert
        assertEquals(1, result.size());
        assertEquals("G1", result.get(0).getId());
        assertEquals(1, result.get(0).getTransactions().size());
        assertEquals("T1", result.get(0).getTransactions().get(0).getId());
    }

    @Test
    void getPostedTransactionGroupsForUser_filtersCorrectly() {
        // Arrange
        User user = new User();
        user.setId("user1");

        TransactionGroup group = new TransactionGroup();
        group.setId("G1");

        Brand brand = new Brand();
        brand.setId("brand1");

        TransactionType type = new TransactionType();
        type.setId("SHOP");

        Bank bank1 = new Bank();
        bank1.setUser(user);
        Bank bank2 = new Bank();
        bank2.setUser(user);

        Transaction tx1 = new Transaction();
        tx1.setId("T1");
        tx1.setBank(bank1);
        tx1.setBrand(brand);
        tx1.setType(type);
        tx1.setDate(LocalDate.of(2024, 1, 1));

        Transaction tx2 = new Transaction();
        tx2.setId("T2");
        tx2.setBank(bank2);
        tx2.setBrand(brand);
        tx2.setType(type);
        tx2.setDate(LocalDate.of(2024, 1, 2));

        when(transactionGroupRepository.findAll()).thenReturn(List.of(group));
        when(transactionRepository.findByGroup(group)).thenReturn(List.of(tx1, tx2));
        when(pendingTransactionRepository.existsByTransactionId("T1")).thenReturn(true);
        when(pendingTransactionRepository.existsByTransactionId("T2")).thenReturn(false);

        // Act
        List<TransactionGroupResponseDto> result = service.getPostedTransactionGroupsForUser(user);

        // Assert
        assertEquals(1, result.size());
        TransactionGroupResponseDto dto = result.get(0);
        assertEquals("G1", dto.getId());
        assertEquals(1, dto.getTransactions().size());
        assertEquals("T2", dto.getTransactions().get(0).getId());
    }


    @Test
    void getPendingTransactionGroupsForUser_returnsEmptyIfNoPending() {
        // Arrange
        User user = new User(); user.setId("user1");
        TransactionGroup group = new TransactionGroup(); group.setId("G1");
        Transaction tx = new Transaction(); tx.setId("T1"); tx.setBank(new Bank() {{ setUser(user); }});

        when(transactionGroupRepository.findAll()).thenReturn(List.of(group));
        when(transactionRepository.findByGroup(group)).thenReturn(List.of(tx));
        when(pendingTransactionRepository.existsByTransactionId("T1")).thenReturn(false);

        // Act
        List<TransactionGroupResponseDto> result = service.getPendingTransactionGroupsForUser(user);

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void getPostedTransactionGroupsForUser_returnsEmptyIfAllPending() {
        // Arrange
        User user = new User(); user.setId("user1");
        TransactionGroup group = new TransactionGroup(); group.setId("G1");
        Transaction tx = new Transaction(); tx.setId("T1"); tx.setBank(new Bank() {{ setUser(user); }});

        when(transactionGroupRepository.findAll()).thenReturn(List.of(group));
        when(transactionRepository.findByGroup(group)).thenReturn(List.of(tx));
        when(pendingTransactionRepository.existsByTransactionId("T1")).thenReturn(true);

        // Act
        List<TransactionGroupResponseDto> result = service.getPostedTransactionGroupsForUser(user);

        // Assert
        assertTrue(result.isEmpty());
    }
}
