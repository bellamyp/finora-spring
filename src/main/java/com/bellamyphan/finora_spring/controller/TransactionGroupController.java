package com.bellamyphan.finora_spring.controller;

import com.bellamyphan.finora_spring.dto.TransactionGroupCreateDto;
import com.bellamyphan.finora_spring.dto.TransactionGroupResponseDto;
import com.bellamyphan.finora_spring.entity.User;
import com.bellamyphan.finora_spring.service.TransactionGroupService;
import com.bellamyphan.finora_spring.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/transaction-groups")
@RequiredArgsConstructor
public class TransactionGroupController {

    private final TransactionGroupService transactionGroupService;
    private final UserService userService;

    /**
     * Get transaction groups for the current user.
     * Optional query parameter: status=pending|posted
     */
    @GetMapping
    public ResponseEntity<List<TransactionGroupResponseDto>> getGroupsForCurrentUser(
            @RequestParam(value = "status", required = false, defaultValue = "posted") String status
    ) {
        // Get user ID from JWT
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));

        List<TransactionGroupResponseDto> groups;

        switch (status.toLowerCase()) {
            case "pending":
                groups = transactionGroupService.getPendingTransactionGroupsForUser(user);
                break;
            case "posted":
                groups = transactionGroupService.getPostedTransactionGroupsForUser(user);
                break;
            default:
                throw new IllegalArgumentException("Invalid status: " + status + ". Must be 'pending' or 'posted'.");
        }

        return ResponseEntity.ok(groups);
    }

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
