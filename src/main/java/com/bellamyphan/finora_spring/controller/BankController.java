package com.bellamyphan.finora_spring.controller;

import com.bellamyphan.finora_spring.dto.BankCreateDto;
import com.bellamyphan.finora_spring.dto.BankDto;
import com.bellamyphan.finora_spring.entity.Bank;
import com.bellamyphan.finora_spring.entity.User;
import com.bellamyphan.finora_spring.service.BankService;
import com.bellamyphan.finora_spring.service.JwtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;


@RestController
@RequestMapping("/api/banks")
@RequiredArgsConstructor
public class BankController {

    private final BankService bankService;
    private final JwtService jwtService;

    // -----------------------
    // GET banks by user token
    // -----------------------
    @GetMapping
    public List<BankDto> getBanksByUser() {
        User user = jwtService.getCurrentUser();
        return bankService.findBanksByUser(user);
    }

    // -----------------------
    // GET a single bank detail with bankId and user's token
    // -----------------------
    @GetMapping("/{id}")
    public ResponseEntity<BankDto> getBankById(@PathVariable String id) {
        User user = jwtService.getCurrentUser();
        Bank bank = bankService.findBankById(id);

        if (!bank.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        BigDecimal balance = bankService.calculateBalance(id);

        BankDto response = new BankDto(
                bank.getId(),
                bank.getGroup().getId(),
                bank.getName(),
                bank.getType().getType(),
                bank.getUser().getEmail(),
                balance
        );

        return ResponseEntity.ok(response);
    }

    // -----------------------
    // POST create new bank
    // -----------------------
    @PostMapping
    public ResponseEntity<BankDto> createNewBank(@Valid @RequestBody BankCreateDto bankCreateDto) {
        // Get username/email from JWT token
        User user = jwtService.getCurrentUser();
        // Save via BankService (handles NanoID)
        BankDto savedBank = bankService.createBank(bankCreateDto, user);
        return new ResponseEntity<>(savedBank, HttpStatus.CREATED);
    }
}
