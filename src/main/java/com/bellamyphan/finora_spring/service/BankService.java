package com.bellamyphan.finora_spring.service;

import com.bellamyphan.finora_spring.dto.BankDailyBalanceDto;
import com.bellamyphan.finora_spring.dto.BankDto;
import com.bellamyphan.finora_spring.dto.BankEditDto;
import com.bellamyphan.finora_spring.entity.*;
import com.bellamyphan.finora_spring.repository.BankGroupRepository;
import com.bellamyphan.finora_spring.repository.BankRepository;
import com.bellamyphan.finora_spring.repository.BankTypeRepository;
import com.bellamyphan.finora_spring.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BankService {

    private final BankRepository bankRepository;
    private final BankGroupRepository bankGroupRepository;
    private final BankTypeRepository bankTypeRepository;
    private final TransactionRepository transactionRepository;
    private final NanoIdService nanoIdService;

    /**
     * Save a new bank with unique 10-char ID
     */
    @Transactional
    public BankDto createBank(BankEditDto bankEditDto, User user) {

        String bankId = nanoIdService.generateUniqueId(bankRepository);

        BankGroup group = bankGroupRepository.findById(bankEditDto.getGroupId())
                .orElseThrow(() -> new RuntimeException("Bank group not found: " + bankEditDto.getGroupId()));

        BankType type = bankTypeRepository.findByType(bankEditDto.getType())
                .orElseThrow(() -> new RuntimeException("Bank type not found: " + bankEditDto.getType().name()));

        Bank bank = new Bank(
                bankId,
                bankEditDto.getName(),
                bankEditDto.getOpeningDate(),
                bankEditDto.getClosingDate(),
                group,
                type,
                user
        );

        bankRepository.save(bank);
        return toSummaryDto(bank);
    }

    @Transactional
    public BankDto updateBank(BankEditDto dto, User user) {

        // Get bank and verify ownership
        Bank bank = getBankOrThrow(dto.getId(), user);

        BankGroup group = bankGroupRepository.findById(dto.getGroupId())
                .orElseThrow(() -> new RuntimeException("Bank group not found: " + dto.getGroupId()));

        BankType type = bankTypeRepository.findByType(dto.getType())
                .orElseThrow(() -> new RuntimeException("Bank type not found: " + dto.getType().name()));

        bank.setName(dto.getName());
        bank.setGroup(group);
        bank.setType(type);

        // 🔒 Optional rule: opening date immutable
        // bank.setOpeningDate(dto.getOpeningDate());
        bank.setClosingDate(dto.getClosingDate());

        return toSummaryDto(bank);
    }

    /**
     * Find all banks belonging to a given user
     */
    public List<BankDto> findBanksByUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        List<Bank> banks = bankRepository.findByUser(user).stream()
                .sorted((b1, b2) -> b1.getGroup().getName().compareToIgnoreCase(b2.getGroup().getName()))
                .toList();
        return banks.stream()
                .map(bank -> {
                    BigDecimal pendingBalance = calculatePendingBalance(bank.getId());
                    BigDecimal postedBalance = calculatePostedBalance(bank.getId());
                    return new BankDto(
                            bank.getId(),
                            bank.getGroup().getId(),
                            bank.getName(),
                            bank.getType().getType(),
                            bank.getUser().getEmail(),
                            pendingBalance,
                            postedBalance
                    );
                })
                .collect(Collectors.toList());
    }

    /**
     * Find active banks for a user (closingDate == null)
     */
    public List<BankDto> findActiveBanksByUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }

        List<Bank> banks = bankRepository.findByUser(user).stream()
                .filter(bank -> bank.getClosingDate() == null) // only active banks
                .sorted(Comparator.comparing(b -> b.getGroup().getName(), String.CASE_INSENSITIVE_ORDER))
                .toList();

        return banks.stream()
                .map(bank -> {
                    BigDecimal pendingBalance = calculatePendingBalance(bank.getId());
                    BigDecimal postedBalance = calculatePostedBalance(bank.getId());
                    return new BankDto(
                            bank.getId(),
                            bank.getGroup().getId(),
                            bank.getName(),
                            bank.getType().getType(),
                            bank.getUser().getEmail(),
                            pendingBalance,
                            postedBalance
                    );
                })
                .collect(Collectors.toList());
    }

    /**
     * Find inactive banks for a user (closingDate != null)
     */
    public List<BankDto> findInactiveBanksByUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }

        List<Bank> banks = bankRepository.findByUser(user).stream()
                .filter(bank -> bank.getClosingDate() != null) // only inactive banks
                .sorted(Comparator.comparing(b -> b.getGroup().getName(), String.CASE_INSENSITIVE_ORDER))
                .toList();

        return banks.stream()
                .map(bank -> {
                    BigDecimal pendingBalance = calculatePendingBalance(bank.getId());
                    BigDecimal postedBalance = calculatePostedBalance(bank.getId());
                    return new BankDto(
                            bank.getId(),
                            bank.getGroup().getId(),
                            bank.getName(),
                            bank.getType().getType(),
                            bank.getUser().getEmail(),
                            pendingBalance,
                            postedBalance
                    );
                })
                .collect(Collectors.toList());
    }


    /**
     * Find a bank using bankId
     */
    public Bank findBankById(String bankId) {
        return bankRepository.findById(bankId)
                .orElseThrow(() -> new RuntimeException("Bank not found: " + bankId));
    }

    public BankDto getBankSummary(String bankId, User user) {

        Bank bank = getBankOrThrow(bankId, user);

        BigDecimal pending = calculatePendingBalance(bankId);
        BigDecimal posted = calculatePostedBalance(bankId);

        BankDto dto = toSummaryDto(bank);
        dto.setPendingBalance(pending);
        dto.setPostedBalance(posted);

        return dto;
    }

    @Transactional(readOnly = true)
    public BankEditDto getBankForEdit(String bankId, User user) {

        Bank bank = getBankOrThrow(bankId, user);

        BankEditDto dto = new BankEditDto();
        dto.setId(bank.getId());
        dto.setName(bank.getName());
        dto.setGroupId(bank.getGroup().getId());
        dto.setOpeningDate(bank.getOpeningDate());
        dto.setClosingDate(bank.getClosingDate());
        dto.setType(bank.getType().getType());

        return dto;
    }

    // Calculate pending bank balance
    public BigDecimal calculatePendingBalance(String bankId) {
        return Optional.ofNullable(
                transactionRepository.calculatePendingBankBalance(bankId)
        ).orElse(BigDecimal.ZERO);
    }

    // Calculate posted bank balance
    public BigDecimal calculatePostedBalance(String bankId) {
        return Optional.ofNullable(
                transactionRepository.calculatePostedBankBalance(bankId)
        ).orElse(BigDecimal.ZERO);
    }

    public List<BankDailyBalanceDto> calculateLastNDaysBalance(String bankId, User user, int days) {

        // Days must be valid, natural number, non zero
        if (days <= 0) {
            throw new IllegalArgumentException("Days must be positive");
        }
        // Check bank ownership
        getBankOrThrow(bankId, user);

        LocalDate today = LocalDate.now();
        LocalDate startDate = today.minusDays(days - 1);

        // 1️⃣ Get starting balance before startDate
        BigDecimal startingBalance = transactionRepository.calculateBalanceBeforeDate(bankId, startDate);

        // 2️⃣ Transactions within the last N days (inclusive)
        List<Transaction> transactions = transactionRepository.findByBankIdAndDateBetweenOrderByDateAsc(
                bankId, startDate, today);

        // 3️⃣ Group transactions by date
        Map<LocalDate, BigDecimal> dailySums = transactions.stream()
                .collect(Collectors.groupingBy(
                        Transaction::getDate,
                        TreeMap::new,
                        Collectors.reducing(BigDecimal.ZERO, Transaction::getAmount, BigDecimal::add)
                ));

        // 4️⃣ Build cumulative balances
        List<BankDailyBalanceDto> dailyBalances = new ArrayList<>();
        BigDecimal runningBalance = startingBalance;

        for (int i = 0; i < days; i++) {
            LocalDate date = startDate.plusDays(i);
            BigDecimal dailyAmount = dailySums.getOrDefault(date, BigDecimal.ZERO);
            runningBalance = runningBalance.add(dailyAmount);
            dailyBalances.add(new BankDailyBalanceDto(date, runningBalance));
        }

        // 5️⃣ Reverse the list so the newest date is on top
        Collections.reverse(dailyBalances);

        return dailyBalances;
    }

    private Bank getBankOrThrow(String bankId, User user) {
        Bank bank = bankRepository.findById(bankId)
                .orElseThrow(() -> new RuntimeException("Bank not found: " + bankId));

        if (!bank.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Access denied to bank: " + bankId);
        }

        return bank;
    }

    private BankDto toSummaryDto(Bank bank) {
        BankDto dto = new BankDto();
        dto.setId(bank.getId());
        dto.setGroupId(bank.getGroup().getId());
        dto.setName(bank.getName());
        dto.setType(bank.getType().getType());
        dto.setEmail(bank.getUser().getEmail());
        return dto;
    }
}
