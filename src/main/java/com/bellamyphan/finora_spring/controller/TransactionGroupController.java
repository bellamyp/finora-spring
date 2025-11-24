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

        List<TransactionGroupResponseDto> groups = switch (status.toLowerCase()) {
            case "pending" -> transactionGroupService.getPendingTransactionGroupsForUser(user);
            case "posted" -> transactionGroupService.getPostedTransactionGroupsForUser(user);
            default ->
                    throw new IllegalArgumentException("Invalid status: " + status + ". Must be 'pending' or 'posted'.");
        };

        return ResponseEntity.ok(groups);
    }

    /**
     * Get a specific transaction group by ID for the current user
     */
    @GetMapping("/{groupId}")
    public ResponseEntity<TransactionGroupResponseDto> getGroupById(
            @PathVariable("groupId") String groupId
    ) {
        // Get current user from JWT
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));

        // Fetch the group for this user
        TransactionGroupResponseDto group = transactionGroupService.getTransactionGroupByIdForUser(groupId, user)
                .orElseThrow(() -> new RuntimeException("Transaction group not found: " + groupId));

        return ResponseEntity.ok(group);
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
