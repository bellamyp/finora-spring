package com.bellamyphan.finora_spring.runner;

import com.bellamyphan.finora_spring.constant.BankTypeEnum;
import com.bellamyphan.finora_spring.constant.RoleEnum;
import com.bellamyphan.finora_spring.constant.TransactionTypeEnum;
import com.bellamyphan.finora_spring.entity.BankType;
import com.bellamyphan.finora_spring.entity.Role;
import com.bellamyphan.finora_spring.entity.TransactionType;
import com.bellamyphan.finora_spring.service.BankTypeService;
import com.bellamyphan.finora_spring.service.RoleService;
import com.bellamyphan.finora_spring.service.TransactionTypeService;
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
    private final BankTypeService bankTypeService;
    private final TransactionTypeService transactionTypeService;

    @Override
    public void run(String... args) {
        initRoles();
        initBankTypes();
        initTransactionTypes();
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
}
