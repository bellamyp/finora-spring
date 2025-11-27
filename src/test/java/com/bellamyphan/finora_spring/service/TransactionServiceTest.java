package com.bellamyphan.finora_spring.service;

import com.bellamyphan.finora_spring.constant.TransactionTypeEnum;
import com.bellamyphan.finora_spring.dto.TransactionResponseDto;
import com.bellamyphan.finora_spring.dto.TransactionSearchDto;
import com.bellamyphan.finora_spring.entity.*;
import com.bellamyphan.finora_spring.repository.PendingTransactionRepository;
import com.bellamyphan.finora_spring.repository.TransactionRepository;
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

    @Mock
    private TransactionRepository transactionRepository;

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

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setId("user123");

        // Criteria API mocks
        when(em.getCriteriaBuilder()).thenReturn(criteriaBuilder);
        when(criteriaBuilder.createQuery(Transaction.class)).thenReturn(criteriaQuery);
        when(criteriaQuery.from(Transaction.class)).thenReturn(root);
        when(criteriaBuilder.conjunction()).thenReturn(predicate);
        when(criteriaQuery.where(any(Predicate.class))).thenReturn(criteriaQuery);
        when(criteriaQuery.orderBy(anyList())).thenReturn(criteriaQuery);
        when(em.createQuery(criteriaQuery)).thenReturn(typedQuery);

        // Needed so builder methods don't blow up
        when(root.get(anyString())).thenReturn(mock(Path.class));
        when(root.get(anyString()).get(anyString())).thenReturn(mock(Path.class));
    }

    // ---------------------------------------------------------------------
    // 1️⃣ Base search test with all mappings validated
    // ---------------------------------------------------------------------
    @Test
    void testSearchTransactions_ReturnsMappedDTOs() {
        TransactionSearchDto searchDto = new TransactionSearchDto();
        searchDto.setKeyword("test");

        // Build Transaction tree
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
        bank.setUser(user); // required for USER filter
        tx.setBank(bank);

        Brand brand = new Brand();
        brand.setId("brand1");
        tx.setBrand(brand);

        TransactionType type = new TransactionType();
        type.setType(TransactionTypeEnum.INCOME);
        tx.setType(type);

        // Mock result list
        when(typedQuery.getResultList()).thenReturn(List.of(tx));
        when(pendingTransactionRepository.existsByTransactionId("tx1")).thenReturn(false);

        List<TransactionResponseDto> results = transactionService.searchTransactions(searchDto, user);

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

        verify(typedQuery).getResultList();
        verify(pendingTransactionRepository).existsByTransactionId("tx1");
    }

    // ---------------------------------------------------------------------
    // 2️⃣ Pending transaction case
    // ---------------------------------------------------------------------
    @Test
    void testSearchTransactions_PendingTransaction() {
        TransactionSearchDto searchDto = new TransactionSearchDto();

        Transaction tx = new Transaction();
        tx.setId("tx2");
        tx.setDate(LocalDate.now());
        tx.setAmount(new BigDecimal("50.00"));
        tx.setNotes("Pending test");

        TransactionGroup group = new TransactionGroup();
        group.setId("group2");
        tx.setGroup(group);

        Bank bank = new Bank();
        bank.setId("bank2");
        bank.setUser(user);
        tx.setBank(bank);

        Brand brand = new Brand();
        brand.setId("brand2");
        tx.setBrand(brand);

        TransactionType type = new TransactionType();
        type.setType(TransactionTypeEnum.INCOME);
        tx.setType(type);

        when(typedQuery.getResultList()).thenReturn(List.of(tx));
        when(pendingTransactionRepository.existsByTransactionId("tx2")).thenReturn(true);

        List<TransactionResponseDto> results = transactionService.searchTransactions(searchDto, user);

        assertFalse(results.get(0).isPosted());
    }
}
