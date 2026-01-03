package com.bellamyphan.finora_spring.service;

import com.bellamyphan.finora_spring.dto.BankCreateDto;
import com.bellamyphan.finora_spring.dto.BankDailyBalanceDto;
import com.bellamyphan.finora_spring.dto.BankDto;
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
    public BankDto createBank(BankCreateDto createDto, User user) {
        String bankId = nanoIdService.generateUniqueId(bankRepository);
        BankGroup group = bankGroupRepository.findById(createDto.getGroupId())
                .orElseThrow(() -> new RuntimeException("Bank group id is not found: " + createDto.getGroupId()));
        BankType type = bankTypeRepository.findByType(createDto.getType())
                .orElseThrow(() -> new RuntimeException("Bank type is not found: " + createDto.getType().name()));
        Bank newBank = new Bank(bankId, createDto.getName(), createDto.getOpeningDate(),
                createDto.getClosingDate(), group, type, user);
        newBank = bankRepository.save(newBank);
        // Return the created bank dto
        BankDto response = new BankDto();
        response.setId(newBank.getId());
        response.setName(newBank.getName());
        response.setType(newBank.getType().getType());
        response.setEmail(newBank.getUser().getEmail());
        return response;
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
     * Find a bank using bankId
     */
    public Bank findBankById(String bankId) {
        return bankRepository.findById(bankId)
                .orElseThrow(() -> new RuntimeException("Bank not found: " + bankId));
    }

    // Calculate pending bank balance
    public BigDecimal calculatePendingBalance(String bankId) {
        return transactionRepository.calculatePendingBankBalance(bankId);
    }

    // Calculate posted bank balance
    public BigDecimal calculatePostedBalance(String bankId) {
        return transactionRepository.calculatePostedBankBalance(bankId);
    }

    public List<BankDailyBalanceDto> calculateLastNDaysBalance(String bankId, int days) {

        if (days <= 0) {
            throw new IllegalArgumentException("Days must be positive");
        }

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
}
