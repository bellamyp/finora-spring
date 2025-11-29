package com.bellamyphan.finora_spring.service;

import com.bellamyphan.finora_spring.dto.BankGroupCreateDto;
import com.bellamyphan.finora_spring.dto.BankGroupDto;
import com.bellamyphan.finora_spring.entity.BankGroup;
import com.bellamyphan.finora_spring.entity.User;
import com.bellamyphan.finora_spring.repository.BankGroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BankGroupService {

    private final BankGroupRepository bankGroupRepository;
    private final NanoIdService nanoIdService;

    public BankGroupDto createBankGroup(BankGroupCreateDto request, User user) {
        // Make sure name is not duplicated for the current user
        if (bankGroupRepository.existsByNameAndUser(request.getName(), user)) {
            throw new IllegalArgumentException("Bank group name already exists for this user");
        }
        String groupId = nanoIdService.generateUniqueId(bankGroupRepository);
        BankGroup group = new BankGroup(groupId, request.getName(), user);
        return BankGroupDto.fromEntity(bankGroupRepository.save(group));
    }

    public List<BankGroupDto> getAllBankGroupsForCurrentUser(User user) {
        List<BankGroup> bankGroups = bankGroupRepository.findAllByUser(user);
        return bankGroups.stream().map(BankGroupDto::fromEntity).toList();
    }
}