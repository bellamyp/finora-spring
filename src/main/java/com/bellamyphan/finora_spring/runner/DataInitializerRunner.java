package com.bellamyphan.finora_spring.runner;

import com.bellamyphan.finora_spring.config.DefaultAccountProperties;
import com.bellamyphan.finora_spring.constant.BankTypeEnum;
import com.bellamyphan.finora_spring.constant.RoleEnum;
import com.bellamyphan.finora_spring.constant.TransactionTypeEnum;
import com.bellamyphan.finora_spring.dto.BrandCreateDto;
import com.bellamyphan.finora_spring.dto.LocationCreateDto;
import com.bellamyphan.finora_spring.dto.UserRequestDto;
import com.bellamyphan.finora_spring.entity.BankType;
import com.bellamyphan.finora_spring.entity.Role;
import com.bellamyphan.finora_spring.entity.TransactionType;
import com.bellamyphan.finora_spring.service.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializerRunner implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializerRunner.class);
    private final DefaultAccountProperties props;

    private final RoleService roleService;
    private final BankTypeService bankTypeService;
    private final UserService userService;
    private final TransactionTypeService transactionTypeService;
    private final LocationService locationService;
    private final BrandService brandService;

    @Override
    public void run(String... args) {
        initRoles();
        initBankTypes();
        initTransactionTypes();
        initLocations();
        initBrands();
        createAdminAccount();
        createUserAccount();
    }

    private void initRoles() {
        for (RoleEnum roleEnum : RoleEnum.values()) {
            if (!roleService.existsByName(roleEnum)) {
                Role role = new Role();
                role.setName(roleEnum);
                roleService.save(role);
                logger.info("✅ Role created: {}", roleEnum.name());
            } else {
                logger.info("ℹ️ Role already exists: {}", roleEnum.name());
            }
        }
    }

    private void initBankTypes() {
        for (BankTypeEnum typeEnum : BankTypeEnum.values()) {
            if (!bankTypeService.existsByType(typeEnum)) {
                BankType type = new BankType();
                type.setType(typeEnum);
                bankTypeService.save(type);
                logger.info("✅ Bank type created: {}", typeEnum.name());
            } else {
                logger.info("ℹ️ Bank type already exists: {}", typeEnum.name());
            }
        }
    }

    private void initTransactionTypes() {
        for (TransactionTypeEnum typeEnum : TransactionTypeEnum.values()) {
            if (!transactionTypeService.existsByType(typeEnum)) {
                TransactionType type  = new TransactionType();
                type.setType(typeEnum);
                transactionTypeService.save(type);
                logger.info("✅ Transaction type created: {}", typeEnum.name());
            } else {
                logger.info("ℹ️ Transaction type already exists: {}", typeEnum.name());
            }
        }
    }

    private void createAdminAccount() {
        try {
            UserRequestDto adminDto = new UserRequestDto(
                    props.getAdmin().getName(),
                    props.getAdmin().getEmail(),
                    props.getAdmin().getPassword(),
                    RoleEnum.ROLE_ADMIN.name()
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
                    RoleEnum.ROLE_USER.name()
            );
            userService.createUser(userDto);
            logger.info("✅ Default USER account created: {}", userDto.getEmail());
        } catch (IllegalArgumentException e) {
            logger.info("ℹ️ USER account already exists or invalid: {}", e.getMessage());
        } catch (Exception e) {
            logger.error("❌ Failed to create USER account: {}", e.getMessage(), e);
        }
    }

    private void initLocations() {
        Object[][] locations = {
                {"Dallas", "Texas"},
                {"Houston", "Texas"},
                {"Austin", "Texas"},
                {"San Antonio", "Texas"},
                {"Fort Worth", "Texas"},
                {"Los Angeles", "California"},
                {"San Diego", "California"},
                {"New York", "New York"},
                {"Chicago", "Illinois"},
                {"Phoenix", "Arizona"},
        };

        for (Object[] loc : locations) {
            String city = (String) loc[0];
            String state = (String) loc[1];

            try {
                LocationCreateDto dto = new LocationCreateDto(city, state);
                locationService.createLocation(dto);
                logger.info("✅ Location created: {}, {}", city, state);
            } catch (IllegalArgumentException e) {
                logger.info("ℹ️ Location already exists: {}, {}", city, state);
            } catch (Exception e) {
                logger.error("❌ Failed to create location: {}, {} -> {}", city, state, e.getMessage(), e);
            }
        }
    }

    private void initBrands() {
        Object[][] brands = {
                {"Netflix", "https://netflix.com"},
                {"Amazon", "https://amazon.com"},
                {"Walmart", "https://walmart.com"},
                {"Pizza Hut", "https://pizzahut.com"},
                {"Chipotle", "https://chipotle.com"},
                {"Starbucks", "https://starbucks.com"},
                {"McDonald's", "https://mcdonalds.com"},
                {"H-E-B", "https://heb.com"},
                {"Target", "https://target.com"},
                {"Spotify", "https://spotify.com"}
        };

        for (Object[] b : brands) {
            String name = (String) b[0];
            String url = (String) b[1];

            try {
                BrandCreateDto dto = new BrandCreateDto(name, url);
                brandService.createBrand(dto);
                logger.info("✅ Brand created: {}", name);
            } catch (IllegalArgumentException e) {
                logger.info("ℹ️ Brand already exists: {}", name);
            } catch (Exception e) {
                logger.error("❌ Failed to create brand: {} -> {}", name, e.getMessage(), e);
            }
        }
    }

}
