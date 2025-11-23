package com.bellamyphan.finora_spring.service;

import com.bellamyphan.finora_spring.constant.TransactionTypeEnum;
import com.bellamyphan.finora_spring.dto.TransactionCreateDto;
import com.bellamyphan.finora_spring.dto.TransactionGroupCreateDto;
import com.bellamyphan.finora_spring.entity.*;
import com.bellamyphan.finora_spring.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class TransactionGroupService {

    private final NanoIdService nanoIdService;
    private final TransactionGroupRepository transactionGroupRepository;
    private final TransactionRepository transactionRepository;
    private final PendingTransactionRepository pendingTransactionRepository;
    private final BrandRepository brandRepository;
    private final TransactionTypeRepository transactionTypeRepository;
    private final BankRepository bankRepository;

    public String  createTransactionGroup(TransactionGroupCreateDto dto) {

        // ---------------- GET REPORT ----------------
        // Todo: get the latest report here to add to the transaction group.

        // ---------------- CREATE GROUP WITH RETRIES ----------------
        TransactionGroup group = new TransactionGroup();
        TransactionGroup savedGroup = saveGroupWithRetry(group);

        // ---------------- RESOLVE LOOKUPS ----------------
        Brand brand = brandRepository.findById(dto.getBrandId())
                .orElseThrow(() -> new RuntimeException("Brand not found: " + dto.getBrandId()));

        TransactionType type = transactionTypeRepository.findByType(TransactionTypeEnum.fromName(dto.getTypeId()))
                .orElseThrow(() -> new RuntimeException("Transaction type not found: " + dto.getTypeId()));

        LocalDate date = LocalDate.parse(dto.getDate());

        // ---------------- CREATE EACH TRANSACTION ----------------
        for (TransactionCreateDto row : dto.getTransactions()) {

            Bank bank = bankRepository.findById(row.getBankId())
                    .orElseThrow(() -> new RuntimeException("Bank not found: " + row.getBankId()));

            Transaction tx = new Transaction();
            tx.setGroup(savedGroup);
            tx.setDate(date);
            tx.setAmount(row.getAmount() == null ? BigDecimal.ZERO : BigDecimal.valueOf(row.getAmount()));
            tx.setNotes(row.getNotes());
            tx.setBank(bank);
            tx.setBrand(brand);
            tx.setType(type);

            // Save transaction and also mark as pending
            Transaction savedTx = saveTransactionWithRetry(tx);
            savePendingTransaction(savedTx);
        }

        return savedGroup.getId();
    }

    // ============================================================
    //   PRIVATE HELPERS
    // ============================================================

    private TransactionGroup saveGroupWithRetry(TransactionGroup group) {
        for (int i = 0; i < 10; i++) {
            try {
                group.setId(nanoIdService.generate());
                return transactionGroupRepository.save(group);
            } catch (DataIntegrityViolationException ignored) {
                // ID collision — retry
            }
        }
        throw new RuntimeException("Failed to generate unique TransactionGroup ID after 10 attempts");
    }

    private Transaction saveTransactionWithRetry(Transaction tx) {
        for (int i = 0; i < 10; i++) {
            try {
                tx.setId(nanoIdService.generate());
                return transactionRepository.save(tx);
            } catch (DataIntegrityViolationException ignored) {
                // collision — retry
            }
        }
        throw new RuntimeException("Failed to generate unique Transaction ID after 10 attempts");
    }

    private PendingTransaction savePendingTransaction(Transaction tx) {
        PendingTransaction pending = new PendingTransaction(tx);
        return pendingTransactionRepository.save(pending);
    }
}
