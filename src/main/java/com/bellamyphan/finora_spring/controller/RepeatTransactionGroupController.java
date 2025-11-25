package com.bellamyphan.finora_spring.controller;

import com.bellamyphan.finora_spring.entity.RepeatTransactionGroup;
import com.bellamyphan.finora_spring.entity.TransactionGroup;
import com.bellamyphan.finora_spring.entity.User;
import com.bellamyphan.finora_spring.service.RepeatTransactionGroupService;
import com.bellamyphan.finora_spring.service.TransactionGroupService;
import com.bellamyphan.finora_spring.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/repeat-groups")
@RequiredArgsConstructor
public class RepeatTransactionGroupController {

    private final RepeatTransactionGroupService repeatTransactionGroupService;
    private final TransactionGroupService groupService;
    private final UserService userService;

    /**
     * GET: Check if group is already marked as repeat.
     */
    @GetMapping("/{groupId}/is-repeat")
    public ResponseEntity<?> isRepeat(@PathVariable String groupId) {
        // Get user from the token
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userService.findById(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        TransactionGroup group = groupService.fetchTransactionGroup(groupId);

        // Ownership check
        List<?> userTransactions = groupService.getUserTransactionsForGroup(group, currentUser);
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
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userService.findById(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        // Fetch group entity
        TransactionGroup group = groupService.fetchTransactionGroup(groupId);

        // Verify ownership via transactions' banks
        List<?> userTransactions = groupService.getUserTransactionsForGroup(group, currentUser);
        if (userTransactions.isEmpty()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("You are not allowed to mark this empty group as repeat");
        }

        // Mark as repeat
        RepeatTransactionGroup repeatGroup = repeatTransactionGroupService.markAsRepeat(group);
        return ResponseEntity.ok(repeatGroup);
    }
}
