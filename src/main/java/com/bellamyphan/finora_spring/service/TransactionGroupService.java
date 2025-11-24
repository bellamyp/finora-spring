package com.bellamyphan.finora_spring.service;

import com.bellamyphan.finora_spring.constant.TransactionTypeEnum;
import com.bellamyphan.finora_spring.dto.TransactionCreateDto;
import com.bellamyphan.finora_spring.dto.TransactionGroupCreateDto;
import com.bellamyphan.finora_spring.dto.TransactionGroupResponseDto;
import com.bellamyphan.finora_spring.dto.TransactionResponseDto;
import com.bellamyphan.finora_spring.entity.*;
import com.bellamyphan.finora_spring.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

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

    // -------------------------------
    // Get pending transactions for user
    // -------------------------------
    public List<TransactionGroupResponseDto> getPendingTransactionGroupsForUser(User user) {
        return transactionGroupRepository.findAll().stream()
                .map(group -> {
                    List<TransactionResponseDto> transactions = transactionRepository
                            .findByGroup(group).stream()
                            .filter(tx -> tx.getBank().getUser().getId().equals(user.getId()))
                            .filter(tx -> pendingTransactionRepository.existsByTransactionId(tx.getId()))
                            .map(this::toDto)
                            .collect(Collectors.toList());

                    if (transactions.isEmpty()) {
                        return null; // mark empty groups for filtering
                    }

                    TransactionGroupResponseDto dto = new TransactionGroupResponseDto();
                    dto.setId(group.getId());
                    dto.setTransactions(transactions);
                    return dto;
                })
                .filter(Objects::nonNull) // remove empty groups
                .collect(Collectors.toList());
    }

    // -------------------------------
    // Get posted transactions for user
    // -------------------------------
    public List<TransactionGroupResponseDto> getPostedTransactionGroupsForUser(User user) {
        return transactionGroupRepository.findAll().stream()
                .map(group -> {
                    List<TransactionResponseDto> transactions = transactionRepository
                            .findByGroup(group).stream()
                            .filter(tx -> tx.getBank().getUser().getId().equals(user.getId()))
                            .filter(tx -> !pendingTransactionRepository.existsByTransactionId(tx.getId()))
                            .map(this::toDto)
                            .collect(Collectors.toList());

                    if (transactions.isEmpty()) return null;

                    // Sort transactions inside group (newest first, null last)
                    transactions.sort(
                            Comparator.comparing(TransactionResponseDto::getDate,
                                            Comparator.nullsLast(Comparator.naturalOrder()))
                                    .reversed()
                    );

                    TransactionGroupResponseDto dto = new TransactionGroupResponseDto();
                    dto.setId(group.getId());
                    dto.setTransactions(transactions);
                    return dto;
                })
                .filter(Objects::nonNull)
                // Now sort groups by each group's latest transaction date
                .sorted((g1, g2) -> {
                    // extract latest date from each group
                    var d1 = g1.getTransactions().stream()
                            .map(TransactionResponseDto::getDate)
                            .filter(Objects::nonNull)
                            .max(Comparator.naturalOrder())
                            .orElse(null);

                    var d2 = g2.getTransactions().stream()
                            .map(TransactionResponseDto::getDate)
                            .filter(Objects::nonNull)
                            .max(Comparator.naturalOrder())
                            .orElse(null);

                    // Manual null-safe compare: newest first, nulls last
                    if (d1 == null && d2 == null) return 0;
                    if (d1 == null) return 1;   // d2 is "newer" (d1 goes after)
                    if (d2 == null) return -1;  // d1 is "newer"
                    return d2.compareTo(d1);    // reverse order: newest first
                })
                .collect(Collectors.toList());
    }

    // -------------------------------
    // Get specific transactions for user
    // -------------------------------
    public Optional<TransactionGroupResponseDto> getTransactionGroupByIdForUser(String groupId, User user) {
        return transactionGroupRepository.findById(groupId)
                .map(group -> {
                    // Filter transactions that belong to this user (via bank)
                    List<TransactionResponseDto> transactions = transactionRepository.findByGroup(group).stream()
                            .filter(tx -> tx.getBank().getUser().getId().equals(user.getId()))
                            .map(this::toDto)
                            .collect(Collectors.toList());

                    if (transactions.isEmpty()) {
                        return null; // no transactions for this user
                    }

                    TransactionGroupResponseDto dto = new TransactionGroupResponseDto();
                    dto.setId(group.getId());
                    dto.setTransactions(transactions);
                    return dto;
                });
    }

    @Transactional
    public String createTransactionGroup(TransactionGroupCreateDto dto) {

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

    @Transactional
    public void updateTransactionGroup(TransactionGroupResponseDto dto, User user) {
        // 1️⃣ Validate input and fetch group
        TransactionGroup group = fetchTransactionGroup(dto.getId());

        // 2️⃣ Fetch current transactions for this user
        List<Transaction> existingTransactions = getUserTransactionsForGroup(group, user);

        // 3️⃣ Handle case: DTO has no transactions → delete all
        if (dto.getTransactions() == null || dto.getTransactions().isEmpty()) {
            deleteAllTransactionsAndGroup(existingTransactions, group);
            return;
        }

        // 4️⃣ Handle updates and additions
        handleUpdatesAndAdditions(dto.getTransactions(), existingTransactions, group);

        // 5️⃣ Delete any transactions removed in DTO
        deleteRemovedTransactions(existingTransactions);
    }

    // ============================================================
    //   PRIVATE HELPERS
    // ============================================================

    private TransactionResponseDto toDto(Transaction tx) {
        TransactionResponseDto txDto = new TransactionResponseDto();
        txDto.setId(tx.getId());
        txDto.setDate(tx.getDate().toString());
        txDto.setAmount(tx.getAmount());
        txDto.setNotes(tx.getNotes());
        txDto.setBankId(tx.getBank().getId());
        txDto.setBrandId(tx.getBrand().getId());
        txDto.setTypeId(tx.getType().getType().name());
        // posted = true if transaction is not pending
        txDto.setPosted(!pendingTransactionRepository.existsByTransactionId(tx.getId()));
        return txDto;
    }

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

    private TransactionGroup fetchTransactionGroup(String groupId) {
        if (groupId == null || groupId.isEmpty()) {
            throw new IllegalArgumentException("Transaction group ID is required");
        }
        return transactionGroupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Transaction group not found: " + groupId));
    }

    private List<Transaction> getUserTransactionsForGroup(TransactionGroup group, User user) {
        return transactionRepository.findByGroup(group).stream()
                .filter(tx -> tx.getBank().getUser().getId().equals(user.getId()))
                .collect(Collectors.toList());
    }

    private void deleteAllTransactionsAndGroup(List<Transaction> transactions, TransactionGroup group) {
        for (Transaction tx : transactions) {
            pendingTransactionRepository.deleteByTransactionId(tx.getId());
            transactionRepository.delete(tx);
        }
        transactionGroupRepository.deleteById(group.getId());
    }

    private void handleUpdatesAndAdditions(List<TransactionResponseDto> txDtos, List<Transaction> existingTransactions, TransactionGroup group) {
        for (TransactionResponseDto txDto : txDtos) {
            Transaction tx;
            Optional<Transaction> existingTxOpt = existingTransactions.stream()
                    .filter(t -> t.getId().equals(txDto.getId()))
                    .findFirst();

            if (existingTxOpt.isPresent()) {
                tx = existingTxOpt.get();
                existingTransactions.remove(tx);
            } else {
                tx = new Transaction();
                tx.setGroup(group);
            }

            resolveLookupsAndSetFields(tx, txDto);

            // Save safely
            Transaction savedTx = existingTxOpt.isPresent() ? transactionRepository.save(tx)
                    : saveTransactionWithRetry(tx);

            updatePendingStatus(savedTx, txDto.isPosted());
        }
    }

    private void resolveLookupsAndSetFields(Transaction tx, TransactionResponseDto txDto) {
        Brand brand = brandRepository.findById(txDto.getBrandId())
                .orElseThrow(() -> new RuntimeException("Brand not found: " + txDto.getBrandId()));
        Bank bank = bankRepository.findById(txDto.getBankId())
                .orElseThrow(() -> new RuntimeException("Bank not found: " + txDto.getBankId()));
        TransactionType type = transactionTypeRepository.findByType(TransactionTypeEnum.fromName(txDto.getTypeId()))
                .orElseThrow(() -> new RuntimeException("Transaction type not found: " + txDto.getTypeId()));

        tx.setDate(LocalDate.parse(txDto.getDate()));
        tx.setAmount(txDto.getAmount());
        tx.setNotes(txDto.getNotes());
        tx.setBrand(brand);
        tx.setBank(bank);
        tx.setType(type);
    }

    private void updatePendingStatus(Transaction tx, boolean isPosted) {
        if (isPosted) {
            pendingTransactionRepository.deleteByTransactionId(tx.getId());
        } else if (!pendingTransactionRepository.existsByTransactionId(tx.getId())) {
            savePendingTransaction(tx);
        }
    }

    private void deleteRemovedTransactions(List<Transaction> remainingTransactions) {
        for (Transaction tx : remainingTransactions) {
            pendingTransactionRepository.deleteByTransactionId(tx.getId());
            transactionRepository.delete(tx);
        }
    }
}
