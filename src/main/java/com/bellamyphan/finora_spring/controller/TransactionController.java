package com.bellamyphan.finora_spring.controller;

import com.bellamyphan.finora_spring.dto.TransactionResponseDto;
import com.bellamyphan.finora_spring.dto.TransactionSearchDto;
import com.bellamyphan.finora_spring.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    // POST endpoint for searching transactions
    @PostMapping("/search")
    public ResponseEntity<List<TransactionResponseDto>> searchTransactions(
            @RequestBody TransactionSearchDto searchDto) {

        List<TransactionResponseDto> results = transactionService.searchTransactions(searchDto);
        return ResponseEntity.ok(results);
    }
}
