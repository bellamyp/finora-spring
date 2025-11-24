package com.bellamyphan.finora_spring.service;

import com.bellamyphan.finora_spring.dto.TransactionResponseDto;
import com.bellamyphan.finora_spring.dto.TransactionSearchDto;
import com.bellamyphan.finora_spring.entity.Transaction;
import com.bellamyphan.finora_spring.repository.PendingTransactionRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
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

    public List<TransactionResponseDto> searchTransactions(TransactionSearchDto searchDto) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Transaction> cq = cb.createQuery(Transaction.class);
        Root<Transaction> transaction = cq.from(Transaction.class);

        Predicate predicate = cb.conjunction();

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // Date filters
        if (StringUtils.hasText(searchDto.getStartDate())) {
            LocalDate start = LocalDate.parse(searchDto.getStartDate(), fmt);
            predicate = cb.and(predicate, cb.greaterThanOrEqualTo(transaction.get("date"), start));
        }
        if (StringUtils.hasText(searchDto.getEndDate())) {
            LocalDate end = LocalDate.parse(searchDto.getEndDate(), fmt);
            predicate = cb.and(predicate, cb.lessThanOrEqualTo(transaction.get("date"), end));
        }

        // Amount filters
        if (searchDto.getMinAmount() != null) {
            predicate = cb.and(predicate, cb.greaterThanOrEqualTo(transaction.get("amount"), searchDto.getMinAmount()));
        }
        if (searchDto.getMaxAmount() != null) {
            predicate = cb.and(predicate, cb.lessThanOrEqualTo(transaction.get("amount"), searchDto.getMaxAmount()));
        }

        // Bank filter
        if (StringUtils.hasText(searchDto.getBankId())) {
            predicate = cb.and(predicate, cb.equal(transaction.get("bank").get("id"), searchDto.getBankId()));
        }

        // Brand filter
        if (StringUtils.hasText(searchDto.getBrandId())) {
            predicate = cb.and(predicate, cb.equal(transaction.get("brand").get("id"), searchDto.getBrandId()));
        }

        // Type filter
        if (StringUtils.hasText(searchDto.getTypeId())) {
            predicate = cb.and(predicate, cb.equal(transaction.get("type").get("id"), searchDto.getTypeId()));
        }

        // Keyword search in notes
        if (StringUtils.hasText(searchDto.getKeyword())) {
            predicate = cb.and(predicate, cb.like(cb.lower(transaction.get("notes")), "%" + searchDto.getKeyword().toLowerCase() + "%"));
        }

        cq.where(predicate);
        cq.orderBy(cb.desc(transaction.get("date"))); // newest first

        TypedQuery<Transaction> query = em.createQuery(cq);
        List<Transaction> results = query.getResultList();

        // Map to DTO — just return actual fields, do NOT modify posted
        return results.stream().map(tx -> {
            TransactionResponseDto dto = new TransactionResponseDto();
            dto.setId(tx.getId());
            dto.setGroupId(tx.getGroup().getId());
            dto.setDate(tx.getDate().toString());
            dto.setAmount(tx.getAmount());
            dto.setNotes(tx.getNotes());
            dto.setBankId(tx.getBank().getId());
            dto.setBrandId(tx.getBrand().getId());
            dto.setTypeId(tx.getType().getType().name());

            // Determine posted from PendingTransaction
            boolean isPending = pendingTransactionRepository.existsByTransactionId(tx.getId());
            dto.setPosted(!isPending); // posted = true if NOT pending

            return dto;
        }).collect(Collectors.toList());
    }
}
