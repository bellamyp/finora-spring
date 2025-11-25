package com.bellamyphan.finora_spring.controller;

import com.bellamyphan.finora_spring.dto.BankCreateDto;
import com.bellamyphan.finora_spring.dto.BankDto;
import com.bellamyphan.finora_spring.entity.Bank;
import com.bellamyphan.finora_spring.entity.BankType;
import com.bellamyphan.finora_spring.entity.User;
import com.bellamyphan.finora_spring.repository.BankTypeRepository;
import com.bellamyphan.finora_spring.service.BankService;
import com.bellamyphan.finora_spring.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/banks")
@RequiredArgsConstructor
public class BankController {

    private final BankTypeRepository bankTypeRepository;
    private final BankService bankService;
    private final UserService userService;

    // -----------------------
    // GET banks by user token
    // -----------------------
    @GetMapping
    public List<BankDto> getBanksByUser() {
        // Get username/email from JWT token
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));

        return bankService.findBanksByUser(user)
                .stream()
                .map(bank -> {
                    BigDecimal balance = bankService.calculateBalance(bank.getId());
                    return new BankDto(
                            bank.getId(),
                            bank.getName(),
                            bank.getType() != null ? bank.getType().getType() : null,
                            bank.getUser() != null ? bank.getUser().getEmail() : "Unknown",
                            balance
                    );
                })
                .collect(Collectors.toList());
    }

    // -----------------------
    // GET a single bank detail with bankId and user's token
    // -----------------------
    @GetMapping("/{id}")
    public ResponseEntity<BankDto> getBankById(@PathVariable String id) {
        // Get the user info from token
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));

        Bank bank = bankService.findBankById(id);

        if (!bank.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        BigDecimal balance = bankService.calculateBalance(id);

        BankDto response = new BankDto(
                bank.getId(),
                bank.getName(),
                bank.getType() != null ? bank.getType().getType() : null,
                bank.getUser().getEmail(),
                balance
        );

        return ResponseEntity.ok(response);
    }

    // -----------------------
    // POST create new bank
    // -----------------------
    @PostMapping
    public ResponseEntity<BankDto> createNewBank(@Valid @RequestBody BankCreateDto dto) {

        // Get username/email from JWT token
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));

        // Find bank type
        BankType bankType = bankTypeRepository.findByType(dto.getType())
                .orElseThrow(() -> new RuntimeException("Bank type not found: " + dto.getType()));

        // Create new bank entity
        Bank bank = new Bank(
                dto.getName(),
                dto.getOpeningDate(),
                dto.getClosingDate(),
                bankType,
                user
        );

        // Save via BankService (handles NanoID)
        Bank savedBank = bankService.createBank(bank);

        // Convert to DTO for response
        BankDto response = new BankDto(
                savedBank.getId(),
                savedBank.getName(),
                savedBank.getType().getType(),
                savedBank.getUser().getEmail()
        );

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
