package com.bellamyphan.finora_spring.controller;

import com.bellamyphan.finora_spring.dto.TransactionResponseDto;
import com.bellamyphan.finora_spring.dto.TransactionSearchDto;
import com.bellamyphan.finora_spring.entity.User;
import com.bellamyphan.finora_spring.service.TransactionService;
import com.bellamyphan.finora_spring.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;
    private final UserService userService;

    /**
     * Get all pending transactions for current user
     */
    @GetMapping("/pending")
    public ResponseEntity<List<TransactionResponseDto>> getPendingTransactions() {
        // Get user ID from JWT
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));

        List<TransactionResponseDto> pendingTxs = transactionService.getPendingTransactionsForUser(user);
        return ResponseEntity.ok(pendingTxs);
    }

    /**
     * POST endpoint for searching transactions
     */
    @PostMapping("/search")
    public ResponseEntity<List<TransactionResponseDto>> searchTransactions(
            @RequestBody TransactionSearchDto searchDto) {

        List<TransactionResponseDto> results = transactionService.searchTransactions(searchDto);
        return ResponseEntity.ok(results);
    }
}
