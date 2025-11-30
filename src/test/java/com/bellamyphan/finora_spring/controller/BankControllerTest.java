package com.bellamyphan.finora_spring.controller;

import com.bellamyphan.finora_spring.constant.BankTypeEnum;
import com.bellamyphan.finora_spring.dto.BankCreateDto;
import com.bellamyphan.finora_spring.dto.BankDto;
import com.bellamyphan.finora_spring.entity.Bank;
import com.bellamyphan.finora_spring.entity.BankGroup;
import com.bellamyphan.finora_spring.entity.BankType;
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
import java.time.LocalDate;
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
    void getBanksByUser_returnsBankListWithBalance() {
        BankType bankType = new BankType(BankTypeEnum.CHECKING);

        Bank bank = new Bank();
        bank.setId("bank123");
        bank.setName("Checking Bank");
        bank.setType(bankType);
        bank.setUser(mockUser);
        bank.setGroup(null); // group can be null or mocked if needed

        BankDto bankDto = new BankDto(
                "bank123",
                "group1",
                "Checking Bank",
                BankTypeEnum.CHECKING,
                "user@example.com",
                BigDecimal.valueOf(1000.50)
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
        assertEquals(BigDecimal.valueOf(1000.50), dto.getBalance());
    }

    @Test
    void getBankById_returnsBankDetails() {
        BankType bankType = new BankType(BankTypeEnum.SAVINGS);

        BankGroup group = new BankGroup();
        group.setId("group1"); // <-- provide a valid groupId

        Bank bank = new Bank();
        bank.setId("bank123");
        bank.setName("Savings Bank");
        bank.setType(bankType);
        bank.setUser(mockUser);
        bank.setGroup(group); // <-- set the group to avoid NPE

        when(bankService.findBankById("bank123")).thenReturn(bank);
        when(bankService.calculateBalance("bank123")).thenReturn(BigDecimal.valueOf(500.75));

        ResponseEntity<BankDto> response = controller.getBankById("bank123");

        assertEquals(HttpStatus.OK, response.getStatusCode());

        BankDto dto = response.getBody();
        assertNotNull(dto);
        assertEquals("bank123", dto.getId());
        assertEquals("group1", dto.getGroupId());
        assertEquals("Savings Bank", dto.getName());
        assertEquals(BankTypeEnum.SAVINGS, dto.getType());
        assertEquals("user@example.com", dto.getEmail());
        assertEquals(BigDecimal.valueOf(500.75), dto.getBalance());
    }

    @Test
    void getBankById_forbiddenIfUserMismatch() {
        User otherUser = new User();
        otherUser.setId("user2");

        Bank bank = new Bank();
        bank.setId("bank123");
        bank.setUser(otherUser);

        when(bankService.findBankById("bank123")).thenReturn(bank);

        ResponseEntity<BankDto> response = controller.getBankById("bank123");
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    void createNewBank_success() {
        BankCreateDto createDto = new BankCreateDto();
        createDto.setName("New Bank");
        createDto.setType(BankTypeEnum.CHECKING);
        createDto.setOpeningDate(LocalDate.now());

        BankDto savedBankDto = new BankDto(
                "bank999",
                "group1", // provide a groupId
                "New Bank",
                BankTypeEnum.CHECKING,
                "user@example.com",
                BigDecimal.ZERO
        );

        when(bankService.createBank(createDto, mockUser)).thenReturn(savedBankDto);

        ResponseEntity<BankDto> response = controller.createNewBank(createDto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        BankDto dto = response.getBody();
        assertNotNull(dto);
        assertEquals("bank999", dto.getId());
        assertEquals("group1", dto.getGroupId());
        assertEquals("New Bank", dto.getName());
        assertEquals(BankTypeEnum.CHECKING, dto.getType());
        assertEquals("user@example.com", dto.getEmail());
        assertEquals(BigDecimal.ZERO, dto.getBalance());
    }
}
