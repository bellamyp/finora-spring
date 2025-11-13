package com.bellamyphan.finora_spring.runner;

import com.bellamyphan.finora_spring.entity.BankType;
import com.bellamyphan.finora_spring.entity.Role;
import com.bellamyphan.finora_spring.entity.TransactionType;
import com.bellamyphan.finora_spring.repository.BankTypeRepository;
import com.bellamyphan.finora_spring.repository.RoleRepository;
import com.bellamyphan.finora_spring.repository.TransactionTypeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

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
        // Arrange: mock DB entries
        Role role = new Role();
        role.setName("ROLE_ADMIN");

        BankType bankType = new BankType();
        bankType.setType("CHECKING");

        TransactionType transactionType = new TransactionType();
        transactionType.setType("Income");

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
    void run_shouldHandleInvalidEnumValuesGracefully() {
        Role invalidRole = new Role();
        invalidRole.setName("ROLE_INVALID");

        when(roleRepository.findAll()).thenReturn(List.of(invalidRole));

        // Should not throw an exception even if enum value is invalid
        enumCheckerRunner.run();

        verify(roleRepository).findAll();
    }
}