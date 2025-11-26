package com.bellamyphan.finora_spring.runner;

import com.bellamyphan.finora_spring.constant.RoleEnum;
import com.bellamyphan.finora_spring.entity.Role;
import com.bellamyphan.finora_spring.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializerRunner implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializerRunner.class);
    private final RoleService roleService;

    @Override
    public void run(String... args) {
        initRoles();
    }

    private void initRoles() {
        for (RoleEnum roleEnum : RoleEnum.values()) {
            String roleName = roleEnum.name(); // get enum name as string
            if (!roleService.existsByName(roleEnum)) {
                Role role = new Role();
                role.setName(roleEnum);
                roleService.save(role);
                logger.info("✅ Role created: {}", roleName);
            } else {
                logger.info("ℹ️ Role already exists: {}", roleName);
            }
        }
    }
}
