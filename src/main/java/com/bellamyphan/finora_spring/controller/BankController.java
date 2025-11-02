package com.bellamyphan.finora_spring.controller;

import com.bellamyphan.finora_spring.dto.BankCreateDto;
import com.bellamyphan.finora_spring.dto.BankDto;
import com.bellamyphan.finora_spring.entity.Bank;
import com.bellamyphan.finora_spring.entity.BankType;
import com.bellamyphan.finora_spring.entity.User;
import com.bellamyphan.finora_spring.repository.BankRepository;
import com.bellamyphan.finora_spring.repository.BankTypeRepository;
import com.bellamyphan.finora_spring.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/banks")
@RequiredArgsConstructor
public class BankController {

    private final BankRepository bankRepository;
    private final BankTypeRepository bankTypeRepository;
    private final UserRepository userRepository;

    // -----------------------
    // GET banks by user email
    // -----------------------
    @GetMapping
    public List<BankDto> getBanksByUserEmail(@RequestParam String email) {
        return bankRepository.findByUser_Email(email) // correct property traversal
                .stream()
                .map(bank -> new BankDto(
                        bank.getId(),
                        bank.getName(),
                        bank.getType() != null ? bank.getType().getBankEnum() : null,
                        bank.getUser() != null ? bank.getUser().getEmail() : "Unknown"
                ))
                .collect(Collectors.toList());
    }

    // -----------------------
    // POST create new bank
    // -----------------------
    @PostMapping
    public ResponseEntity<BankDto> createBank(@RequestBody BankCreateDto dto) {

        // Find user
        User user = userRepository.findByEmail(dto.getUserEmail())
                .orElseThrow(() -> new RuntimeException("User not found: " + dto.getUserEmail()));

        // Find bank type
        BankType bankType = bankTypeRepository.findByType(dto.getType().toString())
                .orElseThrow(() -> new RuntimeException("Bank type not found: " + dto.getType()));

        // Create new bank entity
        Bank bank = new Bank(
                dto.getName(),
                dto.getOpeningDate(),
                dto.getClosingDate(),
                bankType,
                user
        );

        Bank savedBank = bankRepository.save(bank);

        // Convert to DTO
        BankDto response = new BankDto(
                savedBank.getId(),
                savedBank.getName(),
                savedBank.getType().getBankEnum(),
                savedBank.getUser().getEmail()
        );

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
