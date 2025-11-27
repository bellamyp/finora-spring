package com.bellamyphan.finora_spring.service;

import com.bellamyphan.finora_spring.constant.TransactionTypeEnum;
import com.bellamyphan.finora_spring.dto.TransactionCreateDto;
import com.bellamyphan.finora_spring.dto.TransactionGroupCreateDto;
import com.bellamyphan.finora_spring.dto.TransactionGroupResponseDto;
import com.bellamyphan.finora_spring.dto.TransactionResponseDto;
import com.bellamyphan.finora_spring.entity.*;
import com.bellamyphan.finora_spring.repository.*;
import lombok.RequiredArgsConstructor;
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
    private final RepeatTransactionGroupRepository repeatTransactionGroupRepository;
    private final TransactionTypeRepository transactionTypeRepository;
    private final BrandRepository brandRepository;
    private final LocationRepository locationRepository;
    private final BankRepository bankRepository;

    // ============================================================
    // PENDING GROUPS (optimized)
    // ============================================================
    public List<TransactionGroupResponseDto> getPendingTransactionGroupsForUser(User user) {
        List<Transaction> pending = transactionRepository.findPendingByUserId(user.getId());

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
    // POSTED GROUPS (optimized)
    // ============================================================
    public List<TransactionGroupResponseDto> getPostedTransactionGroupsForUser(User user) {
        List<Transaction> allUserTx = transactionRepository.findByUserId(user.getId());
        Set<String> pendingIds = pendingTransactionRepository.findAllTransactionIds();

        List<Transaction> posted = allUserTx.stream()
                .filter(tx -> !pendingIds.contains(tx.getId()))
                .toList();

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
    // REPEAT GROUPS (optimized)
    // ============================================================
    public List<TransactionGroupResponseDto> getRepeatTransactionGroupsForUser(User user) {
        List<RepeatTransactionGroup> repeatGroups = repeatTransactionGroupRepository.findByUserId(user.getId());

        return repeatGroups.stream()
                .map(RepeatTransactionGroup::getGroup)
                .map(group -> {
                    List<Transaction> userTxs = transactionRepository.findByGroupAndBankUserId(group, user.getId());
                    if (userTxs.isEmpty()) return null;

                    TransactionGroupResponseDto dto = new TransactionGroupResponseDto();
                    dto.setId(group.getId());
                    dto.setTransactions(userTxs.stream().map(this::toDto).collect(Collectors.toList()));
                    return dto;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    // ============================================================
    // LOAD GROUP BY ID FOR USER
    // ============================================================
    public Optional<TransactionGroupResponseDto> getTransactionGroupByIdForUser(String groupId, User user) {
        return transactionGroupRepository.findById(groupId)
                .map(group -> {
                    List<TransactionResponseDto> transactions =
                            transactionRepository.findByGroupAndBankUserId(group, user.getId())
                                    .stream()
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
    // CREATE GROUP
    // ============================================================
    @Transactional
    public String createTransactionGroup(TransactionGroupCreateDto dto) {
        // Ensure at least 1 transaction
        if (dto.getTransactions() == null || dto.getTransactions().isEmpty()) {
            throw new IllegalArgumentException("Cannot create a group without at least 1 transaction");
        }

        // Ensure each transaction has a bank
        for (TransactionCreateDto row : dto.getTransactions()) {
            if (row.getBankId() == null || row.getBankId().isEmpty()) {
                throw new IllegalArgumentException("Each transaction must have a bank selected");
            }
        }

        // Create the group
        TransactionGroup group = saveNewGroup(new TransactionGroup());

        for (TransactionCreateDto row : dto.getTransactions()) {

            // Parse date per transaction
            LocalDate date = row.getDate() == null || row.getDate().isEmpty()
                    ? null
                    : LocalDate.parse(row.getDate());

            // Bank is required
            Bank bank = bankRepository.findById(row.getBankId())
                    .orElseThrow(() -> new RuntimeException("Bank not found: " + row.getBankId()));

            // Fetch brand if provided
            Brand brand = null;
            if (row.getBrandId() != null && !row.getBrandId().isEmpty()) {
                brand = brandRepository.findById(row.getBrandId())
                        .orElseThrow(() -> new RuntimeException("Brand not found: " + row.getBrandId()));
            }

            // Fetch location if provided
            Location location = null;
            if (row.getLocationId() != null && !row.getLocationId().isEmpty()) {
                location = locationRepository.findById(row.getLocationId())
                        .orElseThrow(() -> new RuntimeException("Location not found: " + row.getLocationId()));
            }

            // Fetch transaction type
            TransactionType type = null;
            if (row.getTypeId() != null && !row.getTypeId().isEmpty()) {
                type = transactionTypeRepository
                        .findByType(TransactionTypeEnum.fromName(row.getTypeId()))
                        .orElseThrow(() -> new RuntimeException("Transaction type not found: " + row.getTypeId()));
            }

            // Create transaction entity
            Transaction tx = new Transaction();
            tx.setGroup(group);
            tx.setDate(date);
            tx.setAmount(row.getAmount() == null ? BigDecimal.ZERO : row.getAmount());
            tx.setNotes(row.getNotes());
            tx.setBank(bank);
            tx.setBrand(brand);
            tx.setLocation(location);
            tx.setType(type);

            Transaction savedTx = saveNewTransaction(tx);
            savePendingTransaction(savedTx);
        }

        return group.getId();
    }

    // ============================================================
    // UPDATE GROUP
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
        return transactionRepository.findByGroupAndBankUserId(group, user.getId());
    }

    // ============================================================
    // PRIVATE HELPERS
    // ============================================================
    private TransactionResponseDto toDto(Transaction tx) {
        boolean posted = !pendingTransactionRepository.existsByTransactionId(tx.getId());
        return TransactionResponseDto.fromEntity(tx, posted);
    }

    private TransactionGroup saveNewGroup(TransactionGroup group) {
        String newId = nanoIdService.generateUniqueId(transactionGroupRepository);
        group.setId(newId);
        return transactionGroupRepository.save(group);
    }

    private Transaction saveNewTransaction(Transaction tx) {
        String newId = nanoIdService.generateUniqueId(transactionRepository);
        tx.setId(newId);
        return transactionRepository.save(tx);
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
                    : saveNewTransaction(tx);

            updatePendingStatus(savedTx, txDto.isPosted());
        }
    }

    private void resolveLookupsAndSetFields(Transaction tx, TransactionResponseDto txDto) {
        // Bank is required
        Bank bank = bankRepository.findById(txDto.getBankId())
                .orElseThrow(() -> new RuntimeException("Bank not found: " + txDto.getBankId()));

        // Optional fields (can be null)
        Brand brand = null;
        if (txDto.getBrandId() != null && !txDto.getBrandId().isBlank()) {
            brand = brandRepository.findById(txDto.getBrandId())
                    .orElseThrow(() -> new RuntimeException("Brand not found: " + txDto.getBrandId()));
        }

        Location location = null;
        if (txDto.getLocationId() != null && !txDto.getLocationId().isBlank()) {
            location = locationRepository.findById(txDto.getLocationId())
                    .orElseThrow(() -> new RuntimeException("Location not found: " + txDto.getLocationId()));
        }

        TransactionType type = null;
        if (txDto.getTypeId() != null && !txDto.getTypeId().isBlank()) {
            type = transactionTypeRepository
                    .findByType(TransactionTypeEnum.fromName(txDto.getTypeId()))
                    .orElseThrow(() -> new RuntimeException("Transaction type not found: " + txDto.getTypeId()));
        }

        // Date (optional, null/blank allowed)
        if (txDto.getDate() != null && !txDto.getDate().isBlank()) {
            tx.setDate(LocalDate.parse(txDto.getDate()));
        } else {
            tx.setDate(null);
        }

        // Amount and notes can be null
        tx.setAmount(txDto.getAmount());
        tx.setNotes(txDto.getNotes());

        // Set resolved fields
        tx.setBank(bank);
        tx.setBrand(brand);
        tx.setLocation(location);
        tx.setType(type);
    }

    private void updatePendingStatus(Transaction tx, boolean isPosted) {
        if (isPosted) {
            // Validate required fields
            if (tx.getDate() == null ||
                    tx.getAmount() == null ||
                    tx.getBank() == null ||
                    tx.getBrand() == null ||
                    tx.getLocation() == null ||
                    tx.getType() == null) {
                throw new RuntimeException("Cannot post transaction: required fields missing");
            }

            // Remove from pending since it's fully filled
            pendingTransactionRepository.deleteByTransactionId(tx.getId());
        } else if (!pendingTransactionRepository.existsByTransactionId(tx.getId())) {
            // Keep in pending table if not posted
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
