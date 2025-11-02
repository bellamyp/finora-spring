package com.bellamyphan.finora_spring.controller;

import com.bellamyphan.finora_spring.dto.BankDto;
import com.bellamyphan.finora_spring.entity.Bank;
import com.bellamyphan.finora_spring.entity.BankType;
import com.bellamyphan.finora_spring.entity.BankTypeEnum;
import com.bellamyphan.finora_spring.entity.User;
import com.bellamyphan.finora_spring.repository.BankRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BankControllerTest {

    @Mock
    private BankRepository bankRepository;

    @InjectMocks
    private BankController bankController;

    @Test
    void getBanksByUserEmail_ShouldReturnMappedBankDtos() {
        // Arrange
        User user = new User();
        user.setEmail("test@example.com");

        BankType type = new BankType("SAVINGS"); // construct with String

        Bank bank1 = new Bank();
        bank1.setId(1L);
        bank1.setName("Bank A");
        bank1.setType(type);
        bank1.setUser(user);

        Bank bank2 = new Bank();
        bank2.setId(2L);
        bank2.setName("Bank B");
        bank2.setType(null); // test null type
        bank2.setUser(user);

        when(bankRepository.findByUserEmail("test@example.com"))
                .thenReturn(List.of(bank1, bank2));

        // Act
        List<BankDto> result = bankController.getBanksByUserEmail("test@example.com");

        // Assert
        assertEquals(2, result.size());

        BankDto dto1 = result.get(0);
        assertEquals(1L, dto1.getId());
        assertEquals("Bank A", dto1.getName());
        assertEquals(BankTypeEnum.SAVINGS, dto1.getType()); // getter converts String -> enum
        assertEquals("test@example.com", dto1.getEmail());

        BankDto dto2 = result.get(1);
        assertEquals(2L, dto2.getId());
        assertEquals("Bank B", dto2.getName());
        assertNull(dto2.getType()); // type was null
        assertEquals("test@example.com", dto2.getEmail());
    }
}
