package com.bellamyphan.finora_spring.controller;

import com.bellamyphan.finora_spring.dto.TransactionResponseDto;
import com.bellamyphan.finora_spring.dto.TransactionSearchDto;
import com.bellamyphan.finora_spring.entity.User;
import com.bellamyphan.finora_spring.service.JwtService;
import com.bellamyphan.finora_spring.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;
    private final JwtService jwtService;

    /**
     * Get all pending transactions for current user
     */
    @GetMapping("/pending")
    public ResponseEntity<List<TransactionResponseDto>> getPendingTransactions() {
        User user = jwtService.getCurrentUser();
        List<TransactionResponseDto> pendingTxs = transactionService.getPendingTransactionsForUser(user);
        return ResponseEntity.ok(pendingTxs);
    }

    /**
     * POST endpoint for searching transactions
     */
    @PostMapping("/search")
    public ResponseEntity<List<TransactionResponseDto>> searchTransactions(
            @RequestBody TransactionSearchDto searchDto) {
        User user = jwtService.getCurrentUser();
        List<TransactionResponseDto> results = transactionService.searchTransactions(searchDto, user);
        return ResponseEntity.ok(results);
    }
}
