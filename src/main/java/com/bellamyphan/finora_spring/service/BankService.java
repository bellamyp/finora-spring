package com.bellamyphan.finora_spring.service;

import com.bellamyphan.finora_spring.dto.BankCreateDto;
import com.bellamyphan.finora_spring.dto.BankDto;
import com.bellamyphan.finora_spring.entity.Bank;
import com.bellamyphan.finora_spring.entity.BankGroup;
import com.bellamyphan.finora_spring.entity.BankType;
import com.bellamyphan.finora_spring.entity.User;
import com.bellamyphan.finora_spring.repository.BankGroupRepository;
import com.bellamyphan.finora_spring.repository.BankRepository;
import com.bellamyphan.finora_spring.repository.BankTypeRepository;
import com.bellamyphan.finora_spring.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
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
        List<Bank> banks = bankRepository.findByUser(user);
        return banks.stream()
                .map(bank -> {
                    BigDecimal balance = calculateBalance(bank.getId());
                    return new BankDto(
                            bank.getId(),
                            bank.getGroup().getId(),
                            bank.getName(),
                            bank.getType().getType(),
                            bank.getUser().getEmail(),
                            balance
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

    // Calculate bank balance
    public BigDecimal calculateBalance(String bankId) {
        return transactionRepository.calculateBankBalance(bankId);
    }
}
