package com.bellamyphan.finora_spring.controller;

import com.bellamyphan.finora_spring.dto.TransactionGroupCreateDto;
import com.bellamyphan.finora_spring.entity.User;
import com.bellamyphan.finora_spring.service.TransactionGroupService;
import com.bellamyphan.finora_spring.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/transaction-groups")
@RequiredArgsConstructor
public class TransactionGroupController {

    private final UserService userService;
    private final TransactionGroupService transactionGroupService;

    @PostMapping
    public ResponseEntity<?> createTransactionGroup(@RequestBody TransactionGroupCreateDto dto) {
        String groupId = transactionGroupService.createTransactionGroup(dto);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "groupId", groupId,
                "message", "Transaction group created successfully"
        ));
    }
}
