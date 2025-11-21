//package com.bellamyphan.finora_spring.controller;
//
//import com.bellamyphan.finora_spring.dto.TransactionDto;
//import com.bellamyphan.finora_spring.entity.*;
//import com.bellamyphan.finora_spring.repository.TransactionRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.time.LocalDate;
//import java.util.Collections;
//import java.util.List;
//import java.util.UUID;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class TransactionControllerTest {
//
//    @Mock
//    private TransactionRepository transactionRepository;
//
//    @InjectMocks
//    private TransactionController transactionController;
//
//    private User user;
//    private Bank bank;
//    private TransactionType type;
//    private Transaction transaction;
//
//    @BeforeEach
//    void setUp() {
//        user = new User();
//        user.setId(UUID.randomUUID());
//        user.setEmail("bellamyphan@icloud.com");
//
//        bank = new Bank();
//        bank.setId(UUID.randomUUID());
//        bank.setName("Capital One Savings");
//
//        type = new TransactionType();
//        type.setId(UUID.randomUUID());
//        type.setType(TransactionTypeEnum.SAVINGS.getDisplayName());
//
//        transaction = new Transaction();
//        transaction.setId(UUID.randomUUID());
//        transaction.setDate(LocalDate.of(2025, 10, 9));
//        transaction.setAmount(250.0);
//        transaction.setType(type);
//        transaction.setNotes("Savings transfer");
//        transaction.setBank(bank);
//        transaction.setUser(user);
//    }
//
//    // --------------------------------------------------------
//    // ✅ Test: getAllTransactions() when no email is provided
//    // --------------------------------------------------------
//    @Test
//    void testGetAllTransactions_NoEmail() {
//        when(transactionRepository.findAll()).thenReturn(Collections.singletonList(transaction));
//
//        List<TransactionDto> result = transactionController.getAllTransactions(null);
//
//        assertEquals(1, result.size());
//        TransactionDto dto = result.get(0);
//        assertEquals(250.0, dto.getAmount());
//        assertEquals(TransactionTypeEnum.SAVINGS, dto.getType());
//        assertEquals("Capital One Savings", dto.getBankName());
//        assertEquals("bellamyphan@icloud.com", dto.getUserEmail());
//
//        verify(transactionRepository).findAll();
//        verify(transactionRepository, never()).findByUser_Email(anyString());
//    }
//
//    // --------------------------------------------------------
//    // ✅ Test: getAllTransactions() when email is provided
//    // --------------------------------------------------------
//    @Test
//    void testGetAllTransactions_WithEmail() {
//        when(transactionRepository.findByUser_Email("bellamyphan@icloud.com"))
//                .thenReturn(Collections.singletonList(transaction));
//
//        List<TransactionDto> result = transactionController.getAllTransactions("bellamyphan@icloud.com");
//
//        assertEquals(1, result.size());
//        TransactionDto dto = result.get(0);
//        assertEquals(TransactionTypeEnum.SAVINGS, dto.getType());
//        assertEquals("Capital One Savings", dto.getBankName());
//        assertEquals("bellamyphan@icloud.com", dto.getUserEmail());
//
//        verify(transactionRepository).findByUser_Email("bellamyphan@icloud.com");
//        verify(transactionRepository, never()).findAll();
//    }
//
//    // --------------------------------------------------------
//    // ✅ Test: getAllTransactions() when no results
//    // --------------------------------------------------------
//    @Test
//    void testGetAllTransactions_Empty() {
//        when(transactionRepository.findAll()).thenReturn(Collections.emptyList());
//
//        List<TransactionDto> result = transactionController.getAllTransactions(null);
//
//        assertNotNull(result);
//        assertTrue(result.isEmpty());
//    }
//}
