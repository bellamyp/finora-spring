package com.bellamyphan.finora_spring.runner;

import com.bellamyphan.finora_spring.config.DefaultAccountProperties;
import com.bellamyphan.finora_spring.dto.UserRequestDto;
import com.bellamyphan.finora_spring.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DefaultAccountRunner implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DefaultAccountRunner.class);
    private final DefaultAccountProperties props;
    private final UserService userService;

    @Override
    public void run(String... args) {
        createAdminAccount();
        createUserAccount();
    }

    private void createAdminAccount() {
        try {
            UserRequestDto adminDto = new UserRequestDto(
                    props.getAdmin().getName(),
                    props.getAdmin().getEmail(),
                    props.getAdmin().getPassword(),
                    "ROLE_ADMIN"
            );
            userService.createUser(adminDto);
            logger.info("✅ Default ADMIN account created: {}", adminDto.getEmail());
        } catch (IllegalArgumentException e) {
            logger.info("ℹ️ ADMIN account already exists or invalid: {}", e.getMessage());
        } catch (Exception e) {
            logger.error("❌ Failed to create ADMIN account: {}", e.getMessage(), e);
        }
    }

    private void createUserAccount() {
        try {
            UserRequestDto userDto = new UserRequestDto(
                    props.getUser().getName(),
                    props.getUser().getEmail(),
                    props.getUser().getPassword(),
                    "ROLE_USER"
            );
            userService.createUser(userDto);
            logger.info("✅ Default USER account created: {}", userDto.getEmail());
        } catch (IllegalArgumentException e) {
            logger.info("ℹ️ USER account already exists or invalid: {}", e.getMessage());
        } catch (Exception e) {
            logger.error("❌ Failed to create USER account: {}", e.getMessage(), e);
        }
    }
}