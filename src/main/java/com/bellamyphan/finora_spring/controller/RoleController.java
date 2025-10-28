package com.bellamyphan.finora_spring.controller;

import com.bellamyphan.finora_spring.entity.Role;
import com.bellamyphan.finora_spring.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles") // base path for all role endpoints
@RequiredArgsConstructor
public class RoleController {

    private final RoleRepository roleRepository;

    // GET /roles - fetch all roles
    @GetMapping
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

}
