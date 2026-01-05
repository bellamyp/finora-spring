package com.bellamyphan.finora_spring.controller;

import com.bellamyphan.finora_spring.dto.BankDailyBalanceDto;
import com.bellamyphan.finora_spring.dto.BankDto;
import com.bellamyphan.finora_spring.dto.BankEditDto;
import com.bellamyphan.finora_spring.entity.User;
import com.bellamyphan.finora_spring.service.BankService;
import com.bellamyphan.finora_spring.service.JwtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/banks")
@RequiredArgsConstructor
public class BankController {

    private final BankService bankService;
    private final JwtService jwtService;

    // -----------------------
    // GET all banks by user token
    // -----------------------
    @GetMapping
    public List<BankDto> getBanksByUser() {
        User user = jwtService.getCurrentUser();
        return bankService.findBanksByUser(user);
    }

    // -----------------------
    // GET active banks (closingDate == null)
    // -----------------------
    @GetMapping("/active")
    public List<BankDto> getActiveBanks() {
        User user = jwtService.getCurrentUser();
        return bankService.findActiveBanksByUser(user);
    }

    // -----------------------
    // GET inactive banks (closingDate != null)
    // -----------------------
    @GetMapping("/inactive")
    public List<BankDto> getInactiveBanks() {
        User user = jwtService.getCurrentUser();
        return bankService.findInactiveBanksByUser(user);
    }

    // -----------------------
    // GET a single bank summary
    // -----------------------
    @GetMapping("/{id}")
    public ResponseEntity<BankDto> getBankById(@PathVariable String id) {
        User user = jwtService.getCurrentUser();
        BankDto dto = bankService.getBankSummary(id, user);
        return ResponseEntity.ok(dto);
    }


    // -----------------------
    // GET bank for edit
    // -----------------------
    @GetMapping("/{id}/edit")
    public ResponseEntity<BankEditDto> getBankForEdit(@PathVariable String id) {
        User user = jwtService.getCurrentUser();
        BankEditDto dto = bankService.getBankForEdit(id, user);
        return ResponseEntity.ok(dto);
    }

    // -----------------------
    // GET last 30 days daily balance
    // -----------------------
    @GetMapping("/{id}/daily-balance")
    public ResponseEntity<List<BankDailyBalanceDto>> getDailyBalance(@PathVariable String id) {
        User user = jwtService.getCurrentUser();
        // getBankOrThrow is already called inside service if user is invalid
        List<BankDailyBalanceDto> balances = bankService.calculateLastNDaysBalance(id, user, 30);
        return ResponseEntity.ok(balances);
    }

    // -----------------------
    // POST create new bank
    // -----------------------
    @PostMapping
    public ResponseEntity<BankDto> createBank(@RequestBody @Valid BankEditDto bankEditDto) {

        if (bankEditDto.getId() != null) {
            throw new IllegalArgumentException("New bank must not contain id");
        }

        if (bankEditDto.getClosingDate() != null) {
            throw new IllegalArgumentException("New bank cannot have closing date");
        }

        User user = jwtService.getCurrentUser();
        BankDto savedBank = bankService.createBank(bankEditDto, user);

        return new ResponseEntity<>(savedBank, HttpStatus.CREATED);
    }

    // -----------------------
    // PUT update existing bank
    // -----------------------
    @PutMapping
    public ResponseEntity<BankDto> updateBank(@RequestBody @Valid BankEditDto bankEditDto) {

        if (bankEditDto.getId() == null) {
            throw new IllegalArgumentException("Bank DTO must contain id for update");
        }

        User user = jwtService.getCurrentUser();
        BankDto updatedBank = bankService.updateBank(bankEditDto, user);

        return ResponseEntity.ok(updatedBank);
    }
}
