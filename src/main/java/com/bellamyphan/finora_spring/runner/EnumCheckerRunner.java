package com.bellamyphan.finora_spring.runner;

import com.bellamyphan.finora_spring.constant.BankTypeEnum;
import com.bellamyphan.finora_spring.constant.RoleEnum;
import com.bellamyphan.finora_spring.constant.TransactionTypeEnum;
import com.bellamyphan.finora_spring.repository.BankTypeRepository;
import com.bellamyphan.finora_spring.repository.RoleRepository;
import com.bellamyphan.finora_spring.repository.TransactionTypeRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class EnumCheckerRunner implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(EnumCheckerRunner.class);
    private final RoleRepository roleRepository;
    private final BankTypeRepository bankTypeRepository;
    private final TransactionTypeRepository transactionTypeRepository;

    @Override
    public void run(String... args) {
        checkRoles();
        checkBankTypes();
        checkTransactionTypes();
    }

    private void checkRoles() {
        logger.info("---- Checking Roles ----");
        roleRepository.findAll().forEach(role -> {
            RoleEnum roleEnum = role.getName(); // directly a RoleEnum
            if (roleEnum == null) {
                String errorMsg = "❌ RoleEnum is null in DB for Role ID: " + role.getId();
                logger.error(errorMsg);
                throw new IllegalStateException(errorMsg);
            }
            logger.info("✅ Enum match (Role): {}", roleEnum.name());
        });
    }

    private void checkBankTypes() {
        logger.info("---- Checking Bank Types ----");
        bankTypeRepository.findAll().forEach(bankType -> {
            BankTypeEnum typeEnum = bankType.getType(); // get the enum directly
            if (typeEnum == null) {
                String errorMsg = "❌ BankTypeEnum is null in DB for BankType ID: " + bankType.getId();
                logger.error(errorMsg);
                throw new IllegalStateException(errorMsg);
            }
            logger.info("✅ Enum match (Bank Type): {}", typeEnum.name());
        });
    }

    private void checkTransactionTypes() {
        logger.info("---- Checking Transaction Types ----");
        transactionTypeRepository.findAll().forEach(tt -> {
            TransactionTypeEnum typeEnum = tt.getType(); // directly get the enum
            if (typeEnum == null) {
                String errorMsg = "❌ TransactionTypeEnum is null in DB for TransactionType ID: " + tt.getId();
                logger.error(errorMsg);
                throw new IllegalStateException(errorMsg);
            }
            logger.info("✅ Enum match (Transaction Type): {}", typeEnum.name());
        });
    }
}
