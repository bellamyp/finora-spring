package com.bellamyphan.finora_spring.service;

import com.bellamyphan.finora_spring.constant.TransactionTypeEnum;
import com.bellamyphan.finora_spring.dto.TransactionCreateDto;
import com.bellamyphan.finora_spring.dto.TransactionGroupCreateDto;
import com.bellamyphan.finora_spring.dto.TransactionGroupResponseDto;
import com.bellamyphan.finora_spring.entity.*;
import com.bellamyphan.finora_spring.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionGroupServiceTest {

    @Mock TransactionGroupRepository transactionGroupRepository;
    @Mock TransactionRepository transactionRepository;
    @Mock PendingTransactionRepository pendingTransactionRepository;
    @Mock BrandRepository brandRepository;
    @Mock TransactionTypeRepository transactionTypeRepository;
    @Mock BankRepository bankRepository;
    @Mock NanoIdService nanoIdService;

    @InjectMocks TransactionGroupService service;

    // ------------------- PENDING -------------------
    @Test
    void getPendingTransactionGroupsForUser_filtersCorrectly() {
        User user = new User(); user.setId("user1");

        TransactionGroup g1 = new TransactionGroup(); g1.setId("G1");
        TransactionGroup g2 = new TransactionGroup(); g2.setId("G2");

        Brand brand = new Brand(); brand.setId("brand1");
        TransactionType type = new TransactionType(); type.setType(TransactionTypeEnum.fromName("SHOP"));

        Bank b1 = new Bank(); b1.setUser(user); b1.setId("bank1");
        Bank b2 = new Bank(); b2.setUser(user); b2.setId("bank2");

        Transaction t1 = new Transaction();
        t1.setId("T1"); t1.setGroup(g1); t1.setBank(b1);
        t1.setBrand(brand); t1.setType(type); t1.setDate(LocalDate.of(2024,1,1));

        Transaction t2 = new Transaction();
        t2.setId("T2"); t2.setGroup(g2); t2.setBank(b2);
        t2.setBrand(brand); t2.setType(type); t2.setDate(LocalDate.of(2024,1,2));

        when(transactionRepository.findPendingByUserId("user1"))
                .thenReturn(List.of(t1)); // Only t1 is pending

        List<TransactionGroupResponseDto> result = service.getPendingTransactionGroupsForUser(user);

        assertEquals(1, result.size());
        assertEquals("G1", result.get(0).getId());
        assertEquals("T1", result.get(0).getTransactions().get(0).getId());
    }

    // ------------------- POSTED -------------------
    @Test
    void getPostedTransactionGroupsForUser_filtersCorrectly() {

        User user = new User(); user.setId("user1");

        TransactionGroup g = new TransactionGroup(); g.setId("G1");

        Brand brand = new Brand(); brand.setId("brand1");
        TransactionType type = new TransactionType(); type.setType(TransactionTypeEnum.fromName("SHOP"));

        Bank bank = new Bank(); bank.setUser(user); bank.setId("bank1");

        Transaction t1 = new Transaction();
        t1.setId("T1"); t1.setGroup(g); t1.setBank(bank);
        t1.setBrand(brand); t1.setType(type); t1.setDate(LocalDate.of(2024,1,1));

        Transaction t2 = new Transaction();
        t2.setId("T2"); t2.setGroup(g); t2.setBank(bank);
        t2.setBrand(brand); t2.setType(type); t2.setDate(LocalDate.of(2024,1,2));

        when(transactionRepository.findByUserId("user1"))
                .thenReturn(List.of(t1, t2));

        when(pendingTransactionRepository.findAllTransactionIds())
                .thenReturn(Set.of("T1")); // T1 still pending, so only T2 = posted

        List<TransactionGroupResponseDto> result = service.getPostedTransactionGroupsForUser(user);

        assertEquals(1, result.size());
        assertEquals("T2", result.get(0).getTransactions().get(0).getId());
        assertTrue(result.get(0).getTransactions().get(0).isPosted());
    }

    // ------------------- GET GROUP BY ID -------------------
    @Test
    void getTransactionGroupByIdForUser_returnsCorrectTransactions() {

        User user = new User(); user.setId("user1");

        TransactionGroup g = new TransactionGroup(); g.setId("G1");

        Brand brand = new Brand(); brand.setId("brand1");
        TransactionType type = new TransactionType(); type.setType(TransactionTypeEnum.fromName("SHOP"));
        Bank bank = new Bank(); bank.setUser(user); bank.setId("bank1");

        Transaction tx = new Transaction();
        tx.setId("T1"); tx.setGroup(g); tx.setBank(bank);
        tx.setBrand(brand); tx.setType(type); tx.setDate(LocalDate.of(2024,1,1));

        when(transactionGroupRepository.findById("G1")).thenReturn(Optional.of(g));
        when(transactionRepository.findByGroupAndBankUserId(g, "user1"))
                .thenReturn(List.of(tx));

        Optional<TransactionGroupResponseDto> result = service.getTransactionGroupByIdForUser("G1", user);

        assertTrue(result.isPresent());
        assertEquals("T1", result.get().getTransactions().get(0).getId());
    }

    // ------------------- CREATE GROUP -------------------
    @Test
    void createTransactionGroup_savesTransactionsAndMarksPending() {

        TransactionGroupCreateDto dto = new TransactionGroupCreateDto();
        dto.setDate("2024-01-01");
        dto.setBrandId("brand1");
        dto.setTypeId("SHOP");
        dto.setTransactions(List.of(
                new TransactionCreateDto() {{
                    setAmount(100.0);
                    setBankId("bank1");
                    setNotes("note");
                }}
        ));

        Brand brand = new Brand(); brand.setId("brand1");
        TransactionType type = new TransactionType(); type.setType(TransactionTypeEnum.fromName("SHOP"));
        Bank bank = new Bank(); bank.setId("bank1");

        // nanoID generation: 1st = group, 2nd = tx
        when(nanoIdService.generate()).thenReturn("G1", "T1");

        when(transactionGroupRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(transactionRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(pendingTransactionRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        when(brandRepository.findById("brand1")).thenReturn(Optional.of(brand));
        when(transactionTypeRepository.findByType(TransactionTypeEnum.fromName("SHOP"))).thenReturn(Optional.of(type));
        when(bankRepository.findById("bank1")).thenReturn(Optional.of(bank));

        String groupId = service.createTransactionGroup(dto);

        assertEquals("G1", groupId);
        verify(transactionGroupRepository).save(any());
        verify(transactionRepository).save(any());
        verify(pendingTransactionRepository).save(any());
    }
}