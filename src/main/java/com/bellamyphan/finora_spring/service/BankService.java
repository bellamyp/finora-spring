package com.bellamyphan.finora_spring.service;

import com.bellamyphan.finora_spring.entity.Bank;
import com.bellamyphan.finora_spring.entity.User;
import com.bellamyphan.finora_spring.repository.BankRepository;
import com.bellamyphan.finora_spring.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BankService {

    private final BankRepository bankRepository;
    private final TransactionRepository transactionRepository;
    private final NanoIdService nanoIdService;

    /**
     * Save a new bank with unique 10-char ID
     */
    public Bank createBank(Bank bank) {
        String bankId = nanoIdService.generateUniqueId(bankRepository);
        bank.setId(bankId);
        return bankRepository.save(bank);
    }

    /**
     * Find all banks belonging to a given user
     */
    public List<Bank> findBanksByUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        return bankRepository.findByUser(user);
    }

    /**
     * Find a bank using bankId
     */
    public Bank findBankById(String bankId) {
        return bankRepository.findById(bankId)
                .orElseThrow(() -> new RuntimeException("Bank not found: " + bankId));
    }

    // Calculate bank balance
    public BigDecimal calculateBalance(String bankId) {
        return transactionRepository.calculateBankBalance(bankId);
    }
}
