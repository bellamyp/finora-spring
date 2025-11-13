package com.bellamyphan.finora_spring.runner;

import com.bellamyphan.finora_spring.entity.BankTypeEnum;
import com.bellamyphan.finora_spring.entity.RoleEnum;
import com.bellamyphan.finora_spring.entity.TransactionTypeEnum;
import com.bellamyphan.finora_spring.repository.BankTypeRepository;
import com.bellamyphan.finora_spring.repository.RoleRepository;
import com.bellamyphan.finora_spring.repository.TransactionTypeRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.function.Function;

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
        roleRepository.findAll().forEach(role ->
                checkEnumMatchStrict(role.getName(), RoleEnum::fromRoleName, "Role")
        );
    }

    private void checkBankTypes() {
        logger.info("---- Checking Bank Types ----");
        bankTypeRepository.findAll().forEach(bt ->
                checkEnumMatchStrict(bt.getType(), v -> BankTypeEnum.valueOf(v.toUpperCase()), "Bank Type")
        );
    }

    private void checkTransactionTypes() {
        logger.info("---- Checking Transaction Types ----");
        transactionTypeRepository.findAll().forEach(tt ->
                checkEnumMatchStrict(tt.getType(),
                        TransactionTypeEnum::fromDisplayName,
                        "Transaction Type")
        );
    }

    private <E extends Enum<E>> void checkEnumMatchStrict(String dbValue,
                                                          Function<String, E> enumResolver,
                                                          String label) {
        try {
            enumResolver.apply(dbValue);
            logger.info("✅ Enum match ({}): {}", label, dbValue);
        } catch (IllegalArgumentException e) {
            String errorMsg = "❌ Enum mismatch ({}): " + dbValue;
            logger.error(errorMsg, label);
            throw new IllegalStateException(errorMsg);
        }
    }
}
