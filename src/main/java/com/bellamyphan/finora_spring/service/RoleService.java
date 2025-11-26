package com.bellamyphan.finora_spring.service;

import com.bellamyphan.finora_spring.constant.RoleEnum;
import com.bellamyphan.finora_spring.entity.Role;
import com.bellamyphan.finora_spring.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
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
        for (int i = 0; i < 10; i++) {
            try {
                role.setId(nanoIdService.generate());
                return roleRepository.save(role);
            } catch (DataIntegrityViolationException ignored) {
                // Retry if generated ID collides
            }
        }
        throw new RuntimeException("Failed to generate unique Role ID after 10 attempts");
    }
}
