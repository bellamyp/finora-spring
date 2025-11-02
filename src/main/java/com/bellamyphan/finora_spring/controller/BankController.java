package com.bellamyphan.finora_spring.controller;

import com.bellamyphan.finora_spring.dto.BankDto;
import com.bellamyphan.finora_spring.repository.BankRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/banks")
@RequiredArgsConstructor
public class BankController {

    private final BankRepository bankRepository;

    @GetMapping
    public List<BankDto> getBanksByUserEmail(@RequestParam String email) {
        return bankRepository.findByUserEmail(email)
                .stream()
                .map(bank -> new BankDto(
                        bank.getId(),
                        bank.getName(),
                        bank.getType() != null ? bank.getType().getBankEnum() : null,
                        bank.getUser() != null ? bank.getUser().getEmail() : "Unknown"
                ))
                .collect(Collectors.toList());
    }
}
