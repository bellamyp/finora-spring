package com.bellamyphan.finora_spring.controller;

import com.bellamyphan.finora_spring.dto.TransactionGroupResponseDto;
import com.bellamyphan.finora_spring.entity.TransactionGroup;
import com.bellamyphan.finora_spring.entity.User;
import com.bellamyphan.finora_spring.service.JwtService;
import com.bellamyphan.finora_spring.service.RepeatTransactionGroupService;
import com.bellamyphan.finora_spring.service.TransactionGroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/repeat-groups")
@RequiredArgsConstructor
public class RepeatTransactionGroupController {

    private final RepeatTransactionGroupService repeatTransactionGroupService;
    private final TransactionGroupService groupService;
    private final JwtService jwtService;

    /**
     * GET: Check if group is already marked as repeat.
     */
    @GetMapping("/{groupId}/is-repeat")
    public ResponseEntity<?> isRepeat(@PathVariable String groupId) {
        // Get user from the token
        User user = jwtService.getCurrentUser();

        TransactionGroup group = groupService.fetchTransactionGroup(groupId);

        // Ownership check
        List<?> userTransactions = groupService.getUserTransactionsForGroup(group, user);
        if (userTransactions.isEmpty()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("You are not allowed to access this group's repeat status");
        }

        boolean exists = repeatTransactionGroupService.exists(groupId);
        return ResponseEntity.ok(exists);
    }

    @PostMapping("/{groupId}")
    public ResponseEntity<?> markAsRepeat(@PathVariable String groupId) {
        // Get current user
        User user = jwtService.getCurrentUser();

        // Fetch group entity
        TransactionGroup group = groupService.fetchTransactionGroup(groupId);

        // Verify ownership via transactions' banks
        List<?> userTransactions = groupService.getUserTransactionsForGroup(group, user);
        if (userTransactions.isEmpty()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("You are not allowed to mark this empty group as repeat");
        }

        // Mark as repeat
        TransactionGroupResponseDto repeatGroup = repeatTransactionGroupService.markAsRepeat(group, user);
        return ResponseEntity.ok(repeatGroup);
    }

    @DeleteMapping("/{groupId}")
    public ResponseEntity<?> removeRepeat(@PathVariable String groupId) {
        // Get current user
        User user = jwtService.getCurrentUser();

        // Fetch group entity
        TransactionGroup group = groupService.fetchTransactionGroup(groupId);

        // Verify ownership via transactions
        List<?> userTransactions = groupService.getUserTransactionsForGroup(group, user);
        if (userTransactions.isEmpty()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("You are not allowed to remove repeat status from this group");
        }

        // Delete repeat record if exists
        if (repeatTransactionGroupService.exists(groupId)) {
            repeatTransactionGroupService.removeRepeat(group);
            return ResponseEntity.ok("Repeat status removed successfully");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Group is not marked as repeat");
        }
    }
}
