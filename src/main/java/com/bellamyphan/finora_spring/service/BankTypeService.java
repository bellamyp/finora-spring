package com.bellamyphan.finora_spring.service;

import com.bellamyphan.finora_spring.constant.BankTypeEnum;
import com.bellamyphan.finora_spring.entity.BankType;
import com.bellamyphan.finora_spring.repository.BankTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BankTypeService {

    private final BankTypeRepository bankTypeRepository;
    private final NanoIdService nanoIdService;

    public boolean existsByType(BankTypeEnum type) {
        return bankTypeRepository.findByType(type).isPresent();
    }

    public BankType save(BankType type) {
        for (int i = 0; i < 10; i++) {
            try {
                type.setId(nanoIdService.generate());
                return bankTypeRepository.save(type);
            } catch (DataIntegrityViolationException ignored) {
                // Retry if generated ID collides
            }
        }
        throw new RuntimeException("Failed to generate unique BankType ID after 10 attempts");
    }
}
