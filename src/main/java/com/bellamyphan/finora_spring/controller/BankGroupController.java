package com.bellamyphan.finora_spring.controller;

import com.bellamyphan.finora_spring.dto.BankGroupCreateDto;
import com.bellamyphan.finora_spring.dto.BankGroupDto;
import com.bellamyphan.finora_spring.entity.User;
import com.bellamyphan.finora_spring.service.BankGroupService;
import com.bellamyphan.finora_spring.service.JwtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/bank-groups")
public class BankGroupController {

    private final BankGroupService bankGroupService;
    private final JwtService jwtService;

    @GetMapping
    public ResponseEntity<List<BankGroupDto>> getAllBankGroupsForCurrentUser() {
        User currentUser = jwtService.getCurrentUser();
        List<BankGroupDto> groups = bankGroupService.getAllBankGroupsForCurrentUser(currentUser);
        return ResponseEntity.status(HttpStatus.OK).body(groups);
    }

    @PostMapping
    public ResponseEntity<BankGroupDto> createBankGroup(@Valid @RequestBody BankGroupCreateDto request) {
        User currentUser = jwtService.getCurrentUser();
        BankGroupDto groupDto = bankGroupService.createBankGroup(request, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(groupDto);
    }
}