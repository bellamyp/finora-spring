package com.bellamyphan.finora_spring.service;

import com.bellamyphan.finora_spring.constant.TransactionTypeEnum;
import com.bellamyphan.finora_spring.entity.TransactionType;
import com.bellamyphan.finora_spring.repository.TransactionTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TransactionTypeService {

    private final TransactionTypeRepository transactionTypeRepository;
    private final NanoIdService nanoIdService;

    public boolean existsByType(TransactionTypeEnum type) {
        return transactionTypeRepository.findByType(type).isPresent();
    }

    public TransactionType save(TransactionType type) {
        String newId = nanoIdService.generateUniqueId(transactionTypeRepository);
        type.setId(newId);
        return transactionTypeRepository.save(type);
    }
}
