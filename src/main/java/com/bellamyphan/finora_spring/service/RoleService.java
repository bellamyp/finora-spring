package com.bellamyphan.finora_spring.service;

import com.bellamyphan.finora_spring.constant.RoleEnum;
import com.bellamyphan.finora_spring.entity.Role;
import com.bellamyphan.finora_spring.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;
    private final NanoIdService nanoIdService;

    /**
     * Check if a role exists by its enum name.
     */
    public boolean existsByName(RoleEnum name) {
        return roleRepository.findByName(name).isPresent();
    }

    /**
     * Save a Role with a generated unique ID. Retries up to 10 times if ID collision occurs.
     */
    public Role save(Role role) {
        String newId = nanoIdService.generateUniqueId(roleRepository);
        role.setId(newId);
        return roleRepository.save(role);
    }
}
