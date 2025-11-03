package com.bellamyphan.finora_spring.controller;

import com.bellamyphan.finora_spring.dto.TransactionCreateDto;
import com.bellamyphan.finora_spring.dto.TransactionDto;
import com.bellamyphan.finora_spring.entity.Bank;
import com.bellamyphan.finora_spring.entity.Transaction;
import com.bellamyphan.finora_spring.entity.TransactionType;
import com.bellamyphan.finora_spring.entity.User;
import com.bellamyphan.finora_spring.repository.BankRepository;
import com.bellamyphan.finora_spring.repository.TransactionRepository;
import com.bellamyphan.finora_spring.repository.TransactionTypeRepository;
import com.bellamyphan.finora_spring.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionRepository transactionRepository;
    private final TransactionTypeRepository transactionTypeRepository;
    private final UserRepository userRepository;
    private final BankRepository bankRepository;

    // ----------------------------------
    // GET all transactions
    // ----------------------------------
    @GetMapping
    public List<TransactionDto> getAllTransactions(@RequestParam(required = false) String email) {
        List<Transaction> transactions;

        if (email != null && !email.isBlank()) {
            transactions = transactionRepository.findByUser_Email(email);
        } else {
            transactions = transactionRepository.findAll();
        }

        return transactions.stream()
                .map(tx -> new TransactionDto(
                        tx.getId(),
                        tx.getDate(),
                        tx.getAmount(),
                        tx.getType() != null ? tx.getType().getEnum() : null,
                        tx.getNotes(),
                        tx.getBank() != null ? tx.getBank().getName() : null,
                        tx.getUser() != null ? tx.getUser().getEmail() : "Unknown"
                ))
                .collect(Collectors.toList());
    }

    // ----------------------------------
    // CREATE a new transaction
    // ----------------------------------
    @PostMapping
    public TransactionDto createTransaction(@RequestBody TransactionCreateDto dto) {

        // Fetch user by email
        User user = userRepository.findByEmail(dto.getUserEmail())
                .orElseThrow(() -> new IllegalArgumentException("Invalid user email: " + dto.getUserEmail()));

        // Fetch bank if provided and ensure it belongs to the user
        Bank bank = null;
        if (dto.getBankId() != null) {
            bank = bankRepository.findById(dto.getBankId())
                    .filter(b -> b.getUser().getId().equals(user.getId()))
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Invalid bank ID or bank does not belong to user: " + dto.getBankId()));
        }

        // Map enum to TransactionType entity (assumes you have a TransactionType entity storing enums)
        TransactionType type = transactionTypeRepository.findByType(dto.getType().getDisplayName())
                .orElseThrow(() -> new IllegalArgumentException("Invalid transaction type: " + dto.getType()));

        // Create transaction
        Transaction transaction = new Transaction();
        transaction.setDate(dto.getDate());
        transaction.setAmount(dto.getAmount());
        transaction.setType(type);
        transaction.setUser(user);
        transaction.setBank(bank);
        transaction.setNotes(dto.getNotes());

        Transaction saved = transactionRepository.save(transaction);

        // Return DTO
        return new TransactionDto(
                saved.getId(),
                saved.getDate(),
                saved.getAmount(),
                saved.getType() != null ? saved.getType().getEnum() : null,
                saved.getNotes(),
                saved.getBank() != null ? saved.getBank().getName() : null,
                saved.getUser() != null ? saved.getUser().getEmail() : "Unknown"
        );
    }
}
