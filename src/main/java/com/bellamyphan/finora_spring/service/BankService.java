package com.bellamyphan.finora_spring.service;

import com.bellamyphan.finora_spring.entity.Bank;
import com.bellamyphan.finora_spring.entity.User;
import com.bellamyphan.finora_spring.repository.BankRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BankService {

    private final BankRepository bankRepository;
    private final NanoIdService nanoIdService;

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
     * Save a new bank with unique 10-char ID
     */
    public Bank createBank(Bank bank) {
        for (int i = 0; i < 10; i++) {
            try {
                bank.setId(nanoIdService.generate()); // generate 10-char NanoID
                return bankRepository.save(bank);
            } catch (DataIntegrityViolationException ignored) {
                // retry if NanoID collides
            }
        }
        throw new RuntimeException("Failed to generate unique Bank ID after 10 attempts");
    }
}
