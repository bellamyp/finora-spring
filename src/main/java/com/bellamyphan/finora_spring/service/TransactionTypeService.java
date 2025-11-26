package com.bellamyphan.finora_spring.service;

import com.bellamyphan.finora_spring.constant.TransactionTypeEnum;
import com.bellamyphan.finora_spring.entity.TransactionType;
import com.bellamyphan.finora_spring.repository.TransactionTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TransactionTypeService {

    private final TransactionTypeRepository transactionTypeRepository;
    private final NanoIdService nanoIdService;

    public boolean existsByName(TransactionTypeEnum type) {
        return transactionTypeRepository.findByType(type).isPresent();
    }

    public TransactionType save(TransactionType type) {
        for (int i = 0; i < 10; i++) {
            try {
                type.setId(nanoIdService.generate());
                return transactionTypeRepository.save(type);
            } catch (DataIntegrityViolationException ignored) {
                // Retry if generated ID collides
            }
        }
        throw new RuntimeException("Failed to generate unique TransactionType ID after 10 attempts");
    }
}
