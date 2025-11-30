package com.bellamyphan.finora_spring.service;

import com.bellamyphan.finora_spring.dto.TransactionGroupResponseDto;
import com.bellamyphan.finora_spring.entity.RepeatTransactionGroup;
import com.bellamyphan.finora_spring.entity.TransactionGroup;
import com.bellamyphan.finora_spring.entity.User;
import com.bellamyphan.finora_spring.repository.RepeatTransactionGroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RepeatTransactionGroupService {

    private final RepeatTransactionGroupRepository repeatRepository;
    private final TransactionGroupService transactionGroupService;

    /**
     * Marks the given transaction group as repeat.
     * If already marked, returns the existing entry.
     */
    @Transactional
    public TransactionGroupResponseDto markAsRepeat(TransactionGroup group, User user) {
        // Check if already exists
        repeatRepository.findById(group.getId())
                .orElseGet(() -> {
                    RepeatTransactionGroup repeatGroup = new RepeatTransactionGroup(group);
                    return repeatRepository.save(repeatGroup);
                });

        // Return DTO for FE
        return transactionGroupService
                .getTransactionGroupByIdForUser(group.getId(), user)
                .orElseThrow(() -> new RuntimeException("Transaction group not found after marking as repeat"));
    }

    /**
     * Remove repeat flag of the given transaction group.
     */
    @Transactional
    public void removeRepeat(TransactionGroup group) {
        repeatRepository.findById(group.getId()).ifPresent(repeatRepository::delete);
    }

    public boolean exists(String groupId) {
        return repeatRepository.existsById(groupId);
    }
}
