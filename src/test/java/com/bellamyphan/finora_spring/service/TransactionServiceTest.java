package com.bellamyphan.finora_spring.service;

import com.bellamyphan.finora_spring.constant.TransactionTypeEnum;
import com.bellamyphan.finora_spring.dto.TransactionResponseDto;
import com.bellamyphan.finora_spring.dto.TransactionSearchDto;
import com.bellamyphan.finora_spring.entity.*;
import com.bellamyphan.finora_spring.repository.PendingTransactionRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TransactionServiceTest {

    @Mock
    private EntityManager em;

    @Mock
    private PendingTransactionRepository pendingTransactionRepository;

    @InjectMocks
    private TransactionService transactionService;

    @Mock
    private CriteriaBuilder criteriaBuilder;

    @Mock
    private CriteriaQuery<Transaction> criteriaQuery;

    @Mock
    private Root<Transaction> root;

    @Mock
    private Predicate predicate;

    @Mock
    private TypedQuery<Transaction> typedQuery;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // CriteriaBuilder setup
        when(em.getCriteriaBuilder()).thenReturn(criteriaBuilder);
        when(criteriaBuilder.createQuery(Transaction.class)).thenReturn(criteriaQuery);
        when(criteriaQuery.from(Transaction.class)).thenReturn(root);
        when(criteriaBuilder.conjunction()).thenReturn(predicate);
        when(criteriaQuery.where(any(Predicate.class))).thenReturn(criteriaQuery);
        when(criteriaQuery.orderBy(anyList())).thenReturn(criteriaQuery);
        when(em.createQuery(criteriaQuery)).thenReturn(typedQuery);
    }

    @Test
    void testSearchTransactions_ReturnsMappedDTOs() {
        // Arrange
        TransactionSearchDto searchDto = new TransactionSearchDto();
        searchDto.setKeyword("test");

        // Create Transaction entity
        Transaction tx = new Transaction();
        tx.setId("tx1");
        tx.setDate(LocalDate.of(2025, 11, 24));
        tx.setAmount(new BigDecimal("100.00"));
        tx.setNotes("Test notes");

        TransactionGroup group = new TransactionGroup();
        group.setId("group1");
        tx.setGroup(group);

        Bank bank = new Bank();
        bank.setId("bank1");
        tx.setBank(bank);

        Brand brand = new Brand();
        brand.setId("brand1");
        tx.setBrand(brand);

        TransactionType type = new TransactionType();
        type.setType(TransactionTypeEnum.INCOME);
        tx.setType(type);

        when(typedQuery.getResultList()).thenReturn(List.of(tx));
        when(pendingTransactionRepository.existsByTransactionId("tx1")).thenReturn(false);

        // Act
        List<TransactionResponseDto> results = transactionService.searchTransactions(searchDto);

        // Assert
        assertNotNull(results);
        assertEquals(1, results.size());

        TransactionResponseDto dto = results.get(0);
        assertEquals("tx1", dto.getId());
        assertEquals("group1", dto.getGroupId());
        assertEquals("2025-11-24", dto.getDate());
        assertEquals(new BigDecimal("100.00"), dto.getAmount());
        assertEquals("Test notes", dto.getNotes());
        assertEquals("bank1", dto.getBankId());
        assertEquals("brand1", dto.getBrandId());
        assertEquals("INCOME", dto.getTypeId());
        assertTrue(dto.isPosted());

        // Verify repository and query interactions
        verify(typedQuery, times(1)).getResultList();
        verify(pendingTransactionRepository, times(1)).existsByTransactionId("tx1");
    }

    @Test
    void testSearchTransactions_PendingTransaction() {
        // Arrange
        TransactionSearchDto searchDto = new TransactionSearchDto();

        Transaction tx = new Transaction();
        tx.setId("tx2");
        TransactionGroup group = new TransactionGroup();
        group.setId("group2");  // <-- must set ID
        tx.setGroup(group);
        tx.setDate(LocalDate.now());
        tx.setAmount(new BigDecimal("50.00"));
        tx.setNotes("Pending test");
        Bank bank = new Bank();
        bank.setId("bank2");
        tx.setBank(bank);
        Brand brand = new Brand();
        brand.setId("brand2");
        tx.setBrand(brand);
        TransactionType type = new TransactionType();
        type.setType(TransactionTypeEnum.INCOME);
        tx.setType(type);

        when(typedQuery.getResultList()).thenReturn(List.of(tx));
        when(pendingTransactionRepository.existsByTransactionId("tx2")).thenReturn(true);

        // Act
        List<TransactionResponseDto> results = transactionService.searchTransactions(searchDto);

        // Assert
        assertFalse(results.get(0).isPosted());
    }
}
