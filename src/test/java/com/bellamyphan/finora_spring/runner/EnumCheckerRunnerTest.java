package com.bellamyphan.finora_spring.runner;

import com.bellamyphan.finora_spring.constant.BankTypeEnum;
import com.bellamyphan.finora_spring.constant.RoleEnum;
import com.bellamyphan.finora_spring.constant.TransactionTypeEnum;
import com.bellamyphan.finora_spring.entity.BankType;
import com.bellamyphan.finora_spring.entity.Role;
import com.bellamyphan.finora_spring.entity.TransactionType;
import com.bellamyphan.finora_spring.repository.BankTypeRepository;
import com.bellamyphan.finora_spring.repository.RoleRepository;
import com.bellamyphan.finora_spring.repository.TransactionTypeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EnumCheckerRunnerTest {

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private BankTypeRepository bankTypeRepository;

    @Mock
    private TransactionTypeRepository transactionTypeRepository;

    @InjectMocks
    private EnumCheckerRunner enumCheckerRunner;

    @Test
    void run_shouldCheckAllEnums() {
        // Arrange: mock DB entities with enums
        Role role = new Role();
        role.setName(RoleEnum.ROLE_ADMIN);

        BankType bankType = new BankType();
        bankType.setType(BankTypeEnum.CHECKING);

        TransactionType transactionType = new TransactionType();
        transactionType.setType(TransactionTypeEnum.INCOME);

        when(roleRepository.findAll()).thenReturn(List.of(role));
        when(bankTypeRepository.findAll()).thenReturn(List.of(bankType));
        when(transactionTypeRepository.findAll()).thenReturn(List.of(transactionType));

        // Act: run the enum checker
        enumCheckerRunner.run();

        // Assert: repositories were queried
        verify(roleRepository).findAll();
        verify(bankTypeRepository).findAll();
        verify(transactionTypeRepository).findAll();
    }

    @Test
    void run_shouldThrowExceptionForNullEnumValues() {
        // Arrange: invalid Role with null enum
        Role invalidRole = new Role();
        invalidRole.setName(null);

        when(roleRepository.findAll()).thenReturn(List.of(invalidRole));

        // Act & Assert: should throw IllegalStateException
        assertThrows(IllegalStateException.class, () -> enumCheckerRunner.run());

        verify(roleRepository).findAll();
    }

    @Test
    void run_shouldThrowExceptionForNullBankTypeEnum() {
        BankType invalidBankType = new BankType();
        invalidBankType.setType(null);

        when(roleRepository.findAll()).thenReturn(List.of()); // no roles
        when(bankTypeRepository.findAll()).thenReturn(List.of(invalidBankType));

        assertThrows(IllegalStateException.class, () -> enumCheckerRunner.run());
        verify(bankTypeRepository).findAll();
    }

    @Test
    void run_shouldThrowExceptionForNullTransactionTypeEnum() {
        TransactionType invalidTxType = new TransactionType();
        invalidTxType.setType(null);

        when(roleRepository.findAll()).thenReturn(List.of());
        when(bankTypeRepository.findAll()).thenReturn(List.of());
        when(transactionTypeRepository.findAll()).thenReturn(List.of(invalidTxType));

        assertThrows(IllegalStateException.class, () -> enumCheckerRunner.run());
        verify(transactionTypeRepository).findAll();
    }
}
