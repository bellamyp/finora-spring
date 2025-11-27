package com.bellamyphan.finora_spring.service;

import com.bellamyphan.finora_spring.constant.TransactionTypeEnum;
import com.bellamyphan.finora_spring.dto.TransactionResponseDto;
import com.bellamyphan.finora_spring.dto.TransactionSearchDto;
import com.bellamyphan.finora_spring.entity.Transaction;
import com.bellamyphan.finora_spring.entity.User;
import com.bellamyphan.finora_spring.repository.PendingTransactionRepository;
import com.bellamyphan.finora_spring.repository.TransactionRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final EntityManager em;
    private final PendingTransactionRepository pendingTransactionRepository;
    private final TransactionRepository transactionRepository;

    /**
     * Efficient version:
     * Load ONLY pending transactions for this user using a single optimized query.
     */
    public List<TransactionResponseDto> getPendingTransactionsForUser(User user) {
        return transactionRepository.findPendingByUserId(user.getId())
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<TransactionResponseDto> searchTransactions(TransactionSearchDto searchDto, User user) {

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Transaction> cq = cb.createQuery(Transaction.class);
        Root<Transaction> transaction = cq.from(Transaction.class);

        Predicate predicate = cb.conjunction();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // --- Date filters ---
        if (StringUtils.hasText(searchDto.getStartDate())) {
            LocalDate start = LocalDate.parse(searchDto.getStartDate(), fmt);
            predicate = cb.and(predicate, cb.greaterThanOrEqualTo(transaction.get("date"), start));
        }
        if (StringUtils.hasText(searchDto.getEndDate())) {
            LocalDate end = LocalDate.parse(searchDto.getEndDate(), fmt);
            predicate = cb.and(predicate, cb.lessThanOrEqualTo(transaction.get("date"), end));
        }

        // --- Amount filters ---
        if (searchDto.getMinAmount() != null) {
            predicate = cb.and(predicate, cb.greaterThanOrEqualTo(transaction.get("amount"), searchDto.getMinAmount()));
        }
        if (searchDto.getMaxAmount() != null) {
            predicate = cb.and(predicate, cb.lessThanOrEqualTo(transaction.get("amount"), searchDto.getMaxAmount()));
        }

        // --- Bank ---
        if (StringUtils.hasText(searchDto.getBankId())) {
            predicate = cb.and(predicate, cb.equal(transaction.get("bank").get("id"), searchDto.getBankId()));
        }

        // --- Brand ---
        if (StringUtils.hasText(searchDto.getBrandId())) {
            predicate = cb.and(predicate, cb.equal(transaction.get("brand").get("id"), searchDto.getBrandId()));
        }

        // --- Location ---
        if (StringUtils.hasText(searchDto.getLocationId())) {
            predicate = cb.and(predicate, cb.equal(transaction.get("location").get("id"), searchDto.getLocationId()));
        }

        // --- Type ---
        if (StringUtils.hasText(searchDto.getTypeId())) {
            predicate = cb.and(predicate,
                    cb.equal(transaction.get("type").get("type"),
                            TransactionTypeEnum.valueOf(searchDto.getTypeId()))
            );
        }

        // --- Notes keyword search ---
        if (StringUtils.hasText(searchDto.getKeyword())) {
            predicate = cb.and(predicate,
                    cb.like(cb.lower(transaction.get("notes")),
                            "%" + searchDto.getKeyword().toLowerCase() + "%"));
        }

        // --- USER filter: only return transactions for this user ---
        predicate = cb.and(predicate, cb.equal(transaction.get("bank").get("user").get("id"), user.getId()));

        cq.where(predicate);
        cq.orderBy(cb.desc(transaction.get("date"))); // newest first

        List<Transaction> results = em.createQuery(cq).getResultList();

        return results.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }


    // ============================================================
    //   PRIVATE HELPERS
    // ============================================================
    private TransactionResponseDto toDto(Transaction tx) {
        boolean posted = !pendingTransactionRepository.existsByTransactionId(tx.getId());
        return TransactionResponseDto.fromEntity(tx, posted);
    }
}
