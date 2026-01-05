package com.bellamyphan.finora_spring.controller;

import com.bellamyphan.finora_spring.constant.BankTypeEnum;
import com.bellamyphan.finora_spring.dto.BankDto;
import com.bellamyphan.finora_spring.dto.BankEditDto;
import com.bellamyphan.finora_spring.entity.User;
import com.bellamyphan.finora_spring.service.BankService;
import com.bellamyphan.finora_spring.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BankControllerTest {

    @InjectMocks
    private BankController controller;

    @Mock
    private BankService bankService;

    @Mock
    private JwtService jwtService;

    private User mockUser;

    @BeforeEach
    void setup() {
        mockUser = new User();
        mockUser.setId("user1");
        mockUser.setEmail("user@example.com");

        when(jwtService.getCurrentUser()).thenReturn(mockUser);
    }

    @Test
    void getBanksByUser_returnsBankListWithBalances() {
        BankDto bankDto = new BankDto(
                "bank123",
                "group1",
                "Checking Bank",
                BankTypeEnum.CHECKING,
                "user@example.com",
                BigDecimal.valueOf(1000.50),
                BigDecimal.valueOf(2000.75)
        );

        when(bankService.findBanksByUser(mockUser)).thenReturn(List.of(bankDto));

        List<BankDto> result = controller.getBanksByUser();

        assertEquals(1, result.size());
        BankDto dto = result.get(0);
        assertEquals("bank123", dto.getId());
        assertEquals("group1", dto.getGroupId());
        assertEquals("Checking Bank", dto.getName());
        assertEquals(BankTypeEnum.CHECKING, dto.getType());
        assertEquals("user@example.com", dto.getEmail());
        assertEquals(BigDecimal.valueOf(1000.50), dto.getPendingBalance());
        assertEquals(BigDecimal.valueOf(2000.75), dto.getPostedBalance());
    }

    @Test
    void getBankById_returnsBankSummary() {
        BankDto bankDto = new BankDto(
                "bank123",
                "group1",
                "Savings Bank",
                BankTypeEnum.SAVINGS,
                "user@example.com",
                BigDecimal.valueOf(500.75),
                BigDecimal.valueOf(1200.25)
        );

        when(bankService.getBankSummary("bank123", mockUser)).thenReturn(bankDto);

        ResponseEntity<BankDto> response = controller.getBankById("bank123");

        assertEquals(HttpStatus.OK, response.getStatusCode());

        BankDto dto = response.getBody();
        assertNotNull(dto);
        assertEquals("bank123", dto.getId());
        assertEquals("group1", dto.getGroupId());
        assertEquals("Savings Bank", dto.getName());
        assertEquals(BankTypeEnum.SAVINGS, dto.getType());
        assertEquals("user@example.com", dto.getEmail());
        assertEquals(BigDecimal.valueOf(500.75), dto.getPendingBalance());
        assertEquals(BigDecimal.valueOf(1200.25), dto.getPostedBalance());
    }

    @Test
    void getBankForEdit_returnsBankEditDto() {
        BankEditDto editDto = new BankEditDto();
        editDto.setId("bank123");
        editDto.setName("Edit Bank");
        editDto.setType(BankTypeEnum.CHECKING);

        when(bankService.getBankForEdit("bank123", mockUser)).thenReturn(editDto);

        ResponseEntity<BankEditDto> response = controller.getBankForEdit("bank123");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("bank123", response.getBody().getId());
        assertEquals("Edit Bank", response.getBody().getName());
        assertEquals(BankTypeEnum.CHECKING, response.getBody().getType());
    }

    @Test
    void createBank_success() {
        BankEditDto editDto = new BankEditDto();
        editDto.setName("New Bank");
        editDto.setType(BankTypeEnum.CHECKING);

        BankDto savedBankDto = new BankDto(
                "bank999",
                "group1",
                "New Bank",
                BankTypeEnum.CHECKING,
                "user@example.com",
                BigDecimal.ZERO,
                BigDecimal.ZERO
        );

        when(bankService.createBank(editDto, mockUser)).thenReturn(savedBankDto);

        ResponseEntity<BankDto> response = controller.createBank(editDto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        BankDto dto = response.getBody();
        assertNotNull(dto);
        assertEquals("bank999", dto.getId());
        assertEquals("group1", dto.getGroupId());
        assertEquals("New Bank", dto.getName());
        assertEquals(BankTypeEnum.CHECKING, dto.getType());
        assertEquals("user@example.com", dto.getEmail());
        assertEquals(BigDecimal.ZERO, dto.getPendingBalance());
        assertEquals(BigDecimal.ZERO, dto.getPostedBalance());
    }

    @Test
    void updateBank_success() {
        BankEditDto editDto = new BankEditDto();
        editDto.setId("bank123");
        editDto.setName("Updated Bank");
        editDto.setType(BankTypeEnum.SAVINGS);

        BankDto updatedBankDto = new BankDto(
                "bank123",
                "group1",
                "Updated Bank",
                BankTypeEnum.SAVINGS,
                "user@example.com",
                BigDecimal.valueOf(100),
                BigDecimal.valueOf(200)
        );

        when(bankService.updateBank(editDto, mockUser)).thenReturn(updatedBankDto);

        ResponseEntity<BankDto> response = controller.updateBank(editDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        BankDto dto = response.getBody();
        assertNotNull(dto);
        assertEquals("bank123", dto.getId());
        assertEquals("group1", dto.getGroupId());
        assertEquals("Updated Bank", dto.getName());
        assertEquals(BankTypeEnum.SAVINGS, dto.getType());
        assertEquals("user@example.com", dto.getEmail());
        assertEquals(BigDecimal.valueOf(100), dto.getPendingBalance());
        assertEquals(BigDecimal.valueOf(200), dto.getPostedBalance());
    }
}
