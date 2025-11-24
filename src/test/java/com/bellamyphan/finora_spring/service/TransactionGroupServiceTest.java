package com.bellamyphan.finora_spring.service;

import com.bellamyphan.finora_spring.constant.TransactionTypeEnum;
import com.bellamyphan.finora_spring.dto.TransactionCreateDto;
import com.bellamyphan.finora_spring.dto.TransactionGroupCreateDto;
import com.bellamyphan.finora_spring.dto.TransactionGroupResponseDto;
import com.bellamyphan.finora_spring.dto.TransactionResponseDto;
import com.bellamyphan.finora_spring.entity.*;
import com.bellamyphan.finora_spring.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

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
        TransactionGroup group1 = new TransactionGroup(); group1.setId("G1");
        TransactionGroup group2 = new TransactionGroup(); group2.setId("G2");

        Brand brand = new Brand(); brand.setId("brand1");
        TransactionType type = new TransactionType(); type.setType(TransactionTypeEnum.fromName("SHOP"));

        Bank bank1 = new Bank(); bank1.setUser(user); bank1.setId("bank1");
        Bank bank2 = new Bank(); bank2.setUser(user); bank2.setId("bank2");

        Transaction tx1 = new Transaction();
        tx1.setId("T1"); tx1.setBank(bank1); tx1.setBrand(brand); tx1.setType(type); tx1.setDate(LocalDate.of(2024,1,1));

        Transaction tx2 = new Transaction();
        tx2.setId("T2"); tx2.setBank(bank2); tx2.setBrand(brand); tx2.setType(type); tx2.setDate(LocalDate.of(2024,1,2));

        when(transactionGroupRepository.findAll()).thenReturn(List.of(group1, group2));
        when(transactionRepository.findByGroup(group1)).thenReturn(List.of(tx1));
        when(transactionRepository.findByGroup(group2)).thenReturn(List.of(tx2));
        when(pendingTransactionRepository.existsByTransactionId("T1")).thenReturn(true);
        when(pendingTransactionRepository.existsByTransactionId("T2")).thenReturn(false);

        List<TransactionGroupResponseDto> result = service.getPendingTransactionGroupsForUser(user);

        assertEquals(1, result.size());
        TransactionGroupResponseDto dto = result.get(0);
        assertEquals("G1", dto.getId());
        TransactionResponseDto txDto = dto.getTransactions().get(0);
        assertEquals("T1", txDto.getId());
        assertEquals("bank1", txDto.getBankId());
        assertEquals("brand1", txDto.getBrandId());
        assertEquals("SHOP", txDto.getTypeId());
        assertFalse(txDto.isPosted());
    }

    @Test
    void getPendingTransactionGroupsForUser_returnsEmptyIfNoPending() {
        User user = new User(); user.setId("user1");
        TransactionGroup group = new TransactionGroup(); group.setId("G1");
        Transaction tx = new Transaction(); tx.setId("T1"); tx.setBank(new Bank() {{ setUser(user); }});

        when(transactionGroupRepository.findAll()).thenReturn(List.of(group));
        when(transactionRepository.findByGroup(group)).thenReturn(List.of(tx));
        when(pendingTransactionRepository.existsByTransactionId("T1")).thenReturn(false);

        List<TransactionGroupResponseDto> result = service.getPendingTransactionGroupsForUser(user);
        assertTrue(result.isEmpty());
    }

    // ------------------- POSTED -------------------
    @Test
    void getPostedTransactionGroupsForUser_filtersCorrectly() {
        User user = new User(); user.setId("user1");
        TransactionGroup group = new TransactionGroup(); group.setId("G1");

        Brand brand = new Brand(); brand.setId("brand1");
        TransactionType type = new TransactionType(); type.setType(TransactionTypeEnum.fromName("SHOP"));

        Bank bank1 = new Bank(); bank1.setUser(user); bank1.setId("bank1");
        Bank bank2 = new Bank(); bank2.setUser(user); bank2.setId("bank2");

        Transaction tx1 = new Transaction();
        tx1.setId("T1"); tx1.setBank(bank1); tx1.setBrand(brand); tx1.setType(type); tx1.setDate(LocalDate.of(2024,1,1));
        Transaction tx2 = new Transaction();
        tx2.setId("T2"); tx2.setBank(bank2); tx2.setBrand(brand); tx2.setType(type); tx2.setDate(LocalDate.of(2024,1,2));

        when(transactionGroupRepository.findAll()).thenReturn(List.of(group));
        when(transactionRepository.findByGroup(group)).thenReturn(List.of(tx1, tx2));
        when(pendingTransactionRepository.existsByTransactionId("T1")).thenReturn(true);
        when(pendingTransactionRepository.existsByTransactionId("T2")).thenReturn(false);

        List<TransactionGroupResponseDto> result = service.getPostedTransactionGroupsForUser(user);

        assertEquals(1, result.size());
        TransactionGroupResponseDto dto = result.get(0);
        TransactionResponseDto txDto = dto.getTransactions().get(0);
        assertEquals("T2", txDto.getId());
        assertTrue(txDto.isPosted());
    }

    @Test
    void getPostedTransactionGroupsForUser_returnsEmptyIfAllPending() {
        User user = new User(); user.setId("user1");
        TransactionGroup group = new TransactionGroup(); group.setId("G1");
        Transaction tx = new Transaction(); tx.setId("T1"); tx.setBank(new Bank() {{ setUser(user); }});

        when(transactionGroupRepository.findAll()).thenReturn(List.of(group));
        when(transactionRepository.findByGroup(group)).thenReturn(List.of(tx));
        when(pendingTransactionRepository.existsByTransactionId("T1")).thenReturn(true);

        List<TransactionGroupResponseDto> result = service.getPostedTransactionGroupsForUser(user);
        assertTrue(result.isEmpty());
    }

    // ------------------- GET BY ID -------------------
    @Test
    void getTransactionGroupByIdForUser_returnsCorrectTransactions() {
        User user = new User(); user.setId("user1");
        TransactionGroup group = new TransactionGroup(); group.setId("G1");

        Brand brand = new Brand(); brand.setId("brand1");
        TransactionType type = new TransactionType(); type.setType(TransactionTypeEnum.fromName("SHOP"));
        Bank bank = new Bank(); bank.setUser(user); bank.setId("bank1");

        Transaction tx = new Transaction();
        tx.setId("T1"); tx.setBank(bank); tx.setBrand(brand); tx.setType(type); tx.setDate(LocalDate.of(2024,1,1));

        when(transactionGroupRepository.findById("G1")).thenReturn(Optional.of(group));
        when(transactionRepository.findByGroup(group)).thenReturn(List.of(tx));
        when(pendingTransactionRepository.existsByTransactionId("T1")).thenReturn(true);

        Optional<TransactionGroupResponseDto> resultOpt = service.getTransactionGroupByIdForUser("G1", user);
        assertTrue(resultOpt.isPresent());

        TransactionGroupResponseDto dto = resultOpt.get();
        assertEquals("G1", dto.getId());
        TransactionResponseDto txDto = dto.getTransactions().get(0);
        assertEquals("T1", txDto.getId());
        assertFalse(txDto.isPosted());
    }

    // ------------------- CREATE -------------------
    @Test
    void createTransactionGroup_savesTransactionsAndMarksPending() {
        TransactionGroupCreateDto dto = new TransactionGroupCreateDto();
        dto.setDate("2024-01-01");
        dto.setBrandId("brand1");
        dto.setTypeId("SHOP");
        dto.setTransactions(List.of(new TransactionCreateDto() {{
            setAmount(100.0);
            setBankId("bank1");
            setNotes("test");
        }}));

        Brand brand = new Brand(); brand.setId("brand1");
        TransactionType type = new TransactionType(); type.setType(TransactionTypeEnum.fromName("SHOP"));
        Bank bank = new Bank(); bank.setId("bank1");

        when(nanoIdService.generate()).thenReturn("GID", "TID1");
        when(transactionGroupRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(brandRepository.findById("brand1")).thenReturn(Optional.of(brand));
        when(transactionTypeRepository.findByType(TransactionTypeEnum.fromName("SHOP"))).thenReturn(Optional.of(type));
        when(bankRepository.findById("bank1")).thenReturn(Optional.of(bank));
        when(transactionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(pendingTransactionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        String groupId = service.createTransactionGroup(dto);

        assertEquals("GID", groupId);
        verify(transactionGroupRepository, times(1)).save(any());
        verify(transactionRepository, times(1)).save(any());
        verify(pendingTransactionRepository, times(1)).save(any());
    }
}
