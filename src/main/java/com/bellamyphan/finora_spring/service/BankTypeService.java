package com.bellamyphan.finora_spring.service;

import com.bellamyphan.finora_spring.constant.BankTypeEnum;
import com.bellamyphan.finora_spring.entity.BankType;
import com.bellamyphan.finora_spring.repository.BankTypeRepository;
import lombok.RequiredArgsConstructor;
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
        String uniqueId = nanoIdService.generateUniqueId(bankTypeRepository);
        type.setId(uniqueId);
        return bankTypeRepository.save(type);
    }
}
