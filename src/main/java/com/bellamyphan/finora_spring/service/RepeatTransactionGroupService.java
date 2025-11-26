package com.bellamyphan.finora_spring.service;

import com.bellamyphan.finora_spring.entity.RepeatTransactionGroup;
import com.bellamyphan.finora_spring.entity.TransactionGroup;
import com.bellamyphan.finora_spring.repository.RepeatTransactionGroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RepeatTransactionGroupService {

    private final RepeatTransactionGroupRepository repeatRepository;

    /**
     * Marks the given transaction group as repeat.
     * If already marked, returns the existing entry.
     */
    @Transactional
    public RepeatTransactionGroup markAsRepeat(TransactionGroup group) {
        // Check if already exists
        return repeatRepository.findById(group.getId())
                .orElseGet(() -> {
                    RepeatTransactionGroup repeatGroup = new RepeatTransactionGroup(group);
                    return repeatRepository.save(repeatGroup);
                });
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
