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
import java.util.*;
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

    // ============================================================
    //   PENDING GROUPS (optimized)
    // ============================================================
    public List<TransactionGroupResponseDto> getPendingTransactionGroupsForUser(User user) {
        // Load only pending tx for this user
        List<Transaction> pending = transactionRepository.findPendingByUserId(user.getId());

        // Group by TransactionGroup
        Map<TransactionGroup, List<Transaction>> grouped = pending.stream()
                .collect(Collectors.groupingBy(Transaction::getGroup));

        return grouped.entrySet().stream()
                .map(entry -> {
                    TransactionGroupResponseDto dto = new TransactionGroupResponseDto();
                    dto.setId(entry.getKey().getId());
                    dto.setTransactions(entry.getValue().stream()
                            .map(this::toDto)
                            .collect(Collectors.toList()));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    // ============================================================
    //   POSTED GROUPS (optimized)
    // ============================================================
    public List<TransactionGroupResponseDto> getPostedTransactionGroupsForUser(User user) {
        // Load all user transactions efficiently
        List<Transaction> allUserTx = transactionRepository.findByUserId(user.getId());

        // Load pending IDs once
        Set<String> pendingIds = pendingTransactionRepository.findAllTransactionIds();

        // Filter out pending → leaving POSTED ONLY
        List<Transaction> posted = allUserTx.stream()
                .filter(tx -> !pendingIds.contains(tx.getId()))
                .toList();

        // Group by TransactionGroup
        Map<TransactionGroup, List<Transaction>> grouped = posted.stream()
                .collect(Collectors.groupingBy(Transaction::getGroup));

        return grouped.entrySet().stream()
                .map(entry -> {
                    List<TransactionResponseDto> txDtos = entry.getValue().stream()
                            .map(this::toDto)
                            .sorted(Comparator.comparing(TransactionResponseDto::getDate).reversed())
                            .collect(Collectors.toList());

                    TransactionGroupResponseDto dto = new TransactionGroupResponseDto();
                    dto.setId(entry.getKey().getId());
                    dto.setTransactions(txDtos);
                    return dto;
                })
                // Sort groups by newest transaction date
                .sorted((g1, g2) -> {
                    String d1 = g1.getTransactions().stream()
                            .map(TransactionResponseDto::getDate)
                            .max(Comparator.naturalOrder())
                            .orElse(null);

                    String d2 = g2.getTransactions().stream()
                            .map(TransactionResponseDto::getDate)
                            .max(Comparator.naturalOrder())
                            .orElse(null);

                    if (d1 == null && d2 == null) return 0;
                    if (d1 == null) return 1;
                    if (d2 == null) return -1;
                    return d2.compareTo(d1);
                })
                .collect(Collectors.toList());
    }

    // ============================================================
    //   LOAD GROUP BY ID FOR USER
    // ============================================================
    public Optional<TransactionGroupResponseDto> getTransactionGroupByIdForUser(String groupId, User user) {
        return transactionGroupRepository.findById(groupId)
                .map(group -> {
                    List<TransactionResponseDto> transactions = transactionRepository.findByGroup(group).stream()
                            .filter(tx -> tx.getBank().getUser().getId().equals(user.getId()))
                            .map(this::toDto)
                            .collect(Collectors.toList());

                    if (transactions.isEmpty()) return null;

                    TransactionGroupResponseDto dto = new TransactionGroupResponseDto();
                    dto.setId(group.getId());
                    dto.setTransactions(transactions);
                    return dto;
                });
    }

    // ============================================================
    //   CREATE GROUP
    // ============================================================
    @Transactional
    public String createTransactionGroup(TransactionGroupCreateDto dto) {
        TransactionGroup group = saveGroupWithRetry(new TransactionGroup());

        Brand brand = brandRepository.findById(dto.getBrandId())
                .orElseThrow(() -> new RuntimeException("Brand not found: " + dto.getBrandId()));

        TransactionType type = transactionTypeRepository
                .findByType(TransactionTypeEnum.fromName(dto.getTypeId()))
                .orElseThrow(() -> new RuntimeException("Transaction type not found: " + dto.getTypeId()));

        LocalDate date = LocalDate.parse(dto.getDate());

        for (TransactionCreateDto row : dto.getTransactions()) {
            Bank bank = bankRepository.findById(row.getBankId())
                    .orElseThrow(() -> new RuntimeException("Bank not found: " + row.getBankId()));

            Transaction tx = new Transaction();
            tx.setGroup(group);
            tx.setDate(date);
            tx.setAmount(row.getAmount() == null ? BigDecimal.ZERO : BigDecimal.valueOf(row.getAmount()));
            tx.setNotes(row.getNotes());
            tx.setBank(bank);
            tx.setBrand(brand);
            tx.setType(type);

            Transaction savedTx = saveTransactionWithRetry(tx);
            savePendingTransaction(savedTx);
        }

        return group.getId();
    }

    // ============================================================
    //   UPDATE GROUP
    // ============================================================
    @Transactional
    public void updateTransactionGroup(TransactionGroupResponseDto dto, User user) {
        TransactionGroup group = fetchTransactionGroup(dto.getId());
        List<Transaction> existingTransactions = getUserTransactionsForGroup(group, user);

        if (dto.getTransactions() == null || dto.getTransactions().isEmpty()) {
            deleteAllTransactionsAndGroup(existingTransactions, group);
            return;
        }

        handleUpdatesAndAdditions(dto.getTransactions(), existingTransactions, group);
        deleteRemovedTransactions(existingTransactions);
    }

    public TransactionGroup fetchTransactionGroup(String groupId) {
        return transactionGroupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Transaction group not found: " + groupId));
    }

    public List<Transaction> getUserTransactionsForGroup(TransactionGroup group, User user) {
        return transactionRepository.findByGroup(group).stream()
                .filter(tx -> tx.getBank().getUser().getId().equals(user.getId()))
                .collect(Collectors.toList());
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
        txDto.setPosted(!pendingTransactionRepository.existsByTransactionId(tx.getId()));
        return txDto;
    }

    private TransactionGroup saveGroupWithRetry(TransactionGroup group) {
        for (int i = 0; i < 10; i++) {
            try {
                group.setId(nanoIdService.generate());
                return transactionGroupRepository.save(group);
            } catch (DataIntegrityViolationException ignored) {}
        }
        throw new RuntimeException("Failed to generate unique TransactionGroup ID");
    }

    private Transaction saveTransactionWithRetry(Transaction tx) {
        for (int i = 0; i < 10; i++) {
            try {
                tx.setId(nanoIdService.generate());
                return transactionRepository.save(tx);
            } catch (DataIntegrityViolationException ignored) {}
        }
        throw new RuntimeException("Failed to generate unique Transaction ID");
    }

    private PendingTransaction savePendingTransaction(Transaction tx) {
        return pendingTransactionRepository.save(new PendingTransaction(tx));
    }

    private void deleteAllTransactionsAndGroup(List<Transaction> transactions, TransactionGroup group) {
        for (Transaction tx : transactions) {
            pendingTransactionRepository.deleteByTransactionId(tx.getId());
            transactionRepository.delete(tx);
        }
        transactionGroupRepository.delete(group);
    }

    private void handleUpdatesAndAdditions(List<TransactionResponseDto> txDtos,
                                           List<Transaction> existingTransactions,
                                           TransactionGroup group) {
        for (TransactionResponseDto txDto : txDtos) {
            Optional<Transaction> opt = existingTransactions.stream()
                    .filter(t -> t.getId().equals(txDto.getId()))
                    .findFirst();

            Transaction tx;
            if (opt.isPresent()) {
                tx = opt.get();
                existingTransactions.remove(tx);
            } else {
                tx = new Transaction();
                tx.setGroup(group);
            }

            resolveLookupsAndSetFields(tx, txDto);

            Transaction savedTx = opt.isPresent()
                    ? transactionRepository.save(tx)
                    : saveTransactionWithRetry(tx);

            updatePendingStatus(savedTx, txDto.isPosted());
        }
    }

    private void resolveLookupsAndSetFields(Transaction tx, TransactionResponseDto txDto) {
        Brand brand = brandRepository.findById(txDto.getBrandId())
                .orElseThrow(() -> new RuntimeException("Brand not found: " + txDto.getBrandId()));

        Bank bank = bankRepository.findById(txDto.getBankId())
                .orElseThrow(() -> new RuntimeException("Bank not found: " + txDto.getBankId()));

        TransactionType type = transactionTypeRepository
                .findByType(TransactionTypeEnum.fromName(txDto.getTypeId()))
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
