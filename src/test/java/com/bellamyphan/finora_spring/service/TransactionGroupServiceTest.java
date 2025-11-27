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

import java.math.BigDecimal;
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
    @Mock RepeatTransactionGroupRepository repeatTransactionGroupRepository;
    @Mock TransactionTypeRepository transactionTypeRepository;
    @Mock BrandRepository brandRepository;
    @Mock LocationRepository locationRepository;
    @Mock BankRepository bankRepository;

    @Mock NanoIdService nanoIdService;

    @InjectMocks TransactionGroupService service;

    // ======================================================
    // PENDING
    // ======================================================
    @Test
    void getPendingTransactionGroupsForUser_filtersCorrectly() {
        User user = new User(); user.setId("user1");

        TransactionGroup g1 = new TransactionGroup(); g1.setId("G1");

        Bank bank = new Bank(); bank.setUser(user); bank.setId("bank1");

        Transaction t1 = new Transaction();
        t1.setId("T1");
        t1.setGroup(g1);
        t1.setBank(bank);
        t1.setDate(LocalDate.of(2024,1,1));

        when(transactionRepository.findPendingByUserId("user1"))
                .thenReturn(List.of(t1));

        List<TransactionGroupResponseDto> result = service.getPendingTransactionGroupsForUser(user);

        assertEquals(1, result.size());
        assertEquals("G1", result.get(0).getId());
        assertEquals("T1", result.get(0).getTransactions().get(0).getId());
    }

    // ======================================================
    // POSTED
    // ======================================================
    @Test
    void getPostedTransactionGroupsForUser_filtersCorrectly() {
        User user = new User(); user.setId("user1");

        TransactionGroup g = new TransactionGroup(); g.setId("G1");

        Bank bank = new Bank(); bank.setUser(user); bank.setId("bank1");

        Transaction t1 = new Transaction();
        t1.setId("T1"); t1.setGroup(g); t1.setBank(bank);
        t1.setDate(LocalDate.of(2024,1,1));

        Transaction t2 = new Transaction();
        t2.setId("T2"); t2.setGroup(g); t2.setBank(bank);
        t2.setDate(LocalDate.of(2024,1,2));

        when(transactionRepository.findByUserId("user1"))
                .thenReturn(List.of(t1, t2));

        when(pendingTransactionRepository.findAllTransactionIds())
                .thenReturn(Set.of("T1")); // T1 pending → only T2 is posted

        List<TransactionGroupResponseDto> result = service.getPostedTransactionGroupsForUser(user);

        assertEquals(1, result.size());
        assertEquals("T2", result.get(0).getTransactions().get(0).getId());
        assertTrue(result.get(0).getTransactions().get(0).isPosted());
    }

    // ======================================================
    // GET GROUP BY ID
    // ======================================================
    @Test
    void getTransactionGroupByIdForUser_returnsCorrectTransactions() {

        User user = new User(); user.setId("user1");

        TransactionGroup g = new TransactionGroup(); g.setId("G1");

        Bank bank = new Bank(); bank.setUser(user); bank.setId("bank1");

        Transaction tx = new Transaction();
        tx.setId("T1"); tx.setGroup(g); tx.setBank(bank);
        tx.setDate(LocalDate.of(2024,1,1));

        when(transactionGroupRepository.findById("G1")).thenReturn(Optional.of(g));
        when(transactionRepository.findByGroupAndBankUserId(g, "user1"))
                .thenReturn(List.of(tx));

        Optional<TransactionGroupResponseDto> result = service.getTransactionGroupByIdForUser("G1", user);

        assertTrue(result.isPresent());
        assertEquals("T1", result.get().getTransactions().get(0).getId());
    }

    // ======================================================
    // CREATE GROUP
    // ======================================================
    @Test
    void createTransactionGroup_savesTransactionsAndMarksPending() {

        TransactionGroupCreateDto dto = new TransactionGroupCreateDto();
        dto.setTransactions(List.of(
                new TransactionCreateDto() {{
                    setAmount(BigDecimal.valueOf(100.00));
                    setBankId("bank1");
                    setNotes("note");
                    setBrandId("brand1");
                    setLocationId("loc1");
                    setTypeId("SHOP");
                    setDate("2024-01-01");
                }}
        ));

        // ---- mock lookup entities ----
        Brand brand = new Brand(); brand.setId("brand1");
        Location loc = new Location(); loc.setId("loc1");
        TransactionType type = new TransactionType(); type.setType(TransactionTypeEnum.fromName("SHOP"));
        Bank bank = new Bank(); bank.setId("bank1");

        when(brandRepository.findById("brand1")).thenReturn(Optional.of(brand));
        when(locationRepository.findById("loc1")).thenReturn(Optional.of(loc));
        when(transactionTypeRepository.findByType(TransactionTypeEnum.fromName("SHOP")))
                .thenReturn(Optional.of(type));
        when(bankRepository.findById("bank1")).thenReturn(Optional.of(bank));

        // ---- nanoIdService generateUniqueId() ----
        when(nanoIdService.generateUniqueId(transactionGroupRepository))
                .thenReturn("G1");
        when(nanoIdService.generateUniqueId(transactionRepository))
                .thenReturn("T1");

        // ---- save mocks ----
        when(transactionGroupRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(transactionRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(pendingTransactionRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        String groupId = service.createTransactionGroup(dto);

        assertEquals("G1", groupId);
        verify(transactionGroupRepository).save(any());
        verify(transactionRepository).save(any());
        verify(pendingTransactionRepository).save(any());
    }
}
