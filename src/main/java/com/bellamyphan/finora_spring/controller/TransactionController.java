package com.bellamyphan.finora_spring.controller;

import com.bellamyphan.finora_spring.dto.TransactionDto;
import com.bellamyphan.finora_spring.entity.Transaction;
import com.bellamyphan.finora_spring.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionRepository transactionRepository;

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
}
