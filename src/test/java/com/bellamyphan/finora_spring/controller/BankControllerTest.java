package com.bellamyphan.finora_spring.controller;

import com.bellamyphan.finora_spring.constant.BankTypeEnum;
import com.bellamyphan.finora_spring.dto.BankCreateDto;
import com.bellamyphan.finora_spring.dto.BankDto;
import com.bellamyphan.finora_spring.entity.Bank;
import com.bellamyphan.finora_spring.entity.BankType;
import com.bellamyphan.finora_spring.entity.User;
import com.bellamyphan.finora_spring.repository.BankTypeRepository;
import com.bellamyphan.finora_spring.service.BankService;
import com.bellamyphan.finora_spring.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BankControllerTest {

    @InjectMocks
    private BankController controller;

    @Mock
    private BankTypeRepository bankTypeRepository;

    @Mock
    private BankService bankService;

    @Mock
    private UserService userService;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @BeforeEach
    void setupSecurityContext() {
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("user1");
    }

    @Test
    void getBanksByUser_returnsBankListWithBalance() {
        User user = new User();
        user.setId("user1");
        user.setEmail("user@example.com");

        BankType bankType = new BankType();
        bankType.setType(BankTypeEnum.CHECKING);

        Bank bank = new Bank();
        bank.setId("bank123");
        bank.setName("Checking Bank");
        bank.setType(bankType);
        bank.setUser(user);

        when(userService.findById("user1")).thenReturn(Optional.of(user));
        when(bankService.findBanksByUser(user)).thenReturn(List.of(bank));
        when(bankService.calculateBalance("bank123")).thenReturn(BigDecimal.valueOf(1000.50));

        List<BankDto> result = controller.getBanksByUser();

        assertEquals(1, result.size());
        BankDto dto = result.get(0);
        assertEquals("bank123", dto.getId());
        assertEquals("Checking Bank", dto.getName());
        assertEquals(BankTypeEnum.CHECKING, dto.getType());
        assertEquals("user@example.com", dto.getEmail());
        assertEquals(BigDecimal.valueOf(1000.50), dto.getBalance());
    }

    @Test
    void getBankById_returnsBankDetails() {
        User user = new User();
        user.setId("user1");
        user.setEmail("user@example.com");

        BankType bankType = new BankType();
        bankType.setType(BankTypeEnum.SAVINGS);

        Bank bank = new Bank();
        bank.setId("bank123");
        bank.setName("Savings Bank");
        bank.setType(bankType);
        bank.setUser(user);

        when(userService.findById("user1")).thenReturn(Optional.of(user));
        when(bankService.findBankById("bank123")).thenReturn(bank);
        when(bankService.calculateBalance("bank123")).thenReturn(BigDecimal.valueOf(500.75));

        ResponseEntity<BankDto> response = controller.getBankById("bank123");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        BankDto dto = response.getBody();
        assertNotNull(dto);
        assertEquals("bank123", dto.getId());
        assertEquals("Savings Bank", dto.getName());
        assertEquals(BankTypeEnum.SAVINGS, dto.getType());
        assertEquals("user@example.com", dto.getEmail());
        assertEquals(BigDecimal.valueOf(500.75), dto.getBalance());
    }

    @Test
    void getBankById_forbiddenIfUserMismatch() {
        User user = new User();
        user.setId("user1");

        User otherUser = new User();
        otherUser.setId("user2");

        Bank bank = new Bank();
        bank.setId("bank123");
        bank.setUser(otherUser);

        when(userService.findById("user1")).thenReturn(Optional.of(user));
        when(bankService.findBankById("bank123")).thenReturn(bank);

        ResponseEntity<BankDto> response = controller.getBankById("bank123");
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    void createNewBank_success() {
        User user = new User();
        user.setId("user1");
        user.setEmail("user@example.com");

        BankType bankType = new BankType();
        bankType.setType(BankTypeEnum.CHECKING);

        BankCreateDto createDto = new BankCreateDto();
        createDto.setName("New Bank");
        createDto.setType(BankTypeEnum.CHECKING);
        createDto.setOpeningDate(LocalDate.now());

        Bank savedBank = new Bank();
        savedBank.setId("bank999");
        savedBank.setName("New Bank");
        savedBank.setType(bankType);
        savedBank.setUser(user);

        when(userService.findById("user1")).thenReturn(Optional.of(user));
        when(bankTypeRepository.findByType(BankTypeEnum.CHECKING)).thenReturn(Optional.of(bankType));
        when(bankService.createBank(any(Bank.class))).thenReturn(savedBank);

        ResponseEntity<BankDto> response = controller.createNewBank(createDto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        BankDto dto = response.getBody();
        assertNotNull(dto);
        assertEquals("bank999", dto.getId());
        assertEquals("New Bank", dto.getName());
        assertEquals(BankTypeEnum.CHECKING, dto.getType());
        assertEquals("user@example.com", dto.getEmail());
    }

    @Test
    void createNewBank_throwsIfBankTypeNotFound() {
        User user = new User();
        user.setId("user1");
        when(userService.findById("user1")).thenReturn(Optional.of(user));

        BankCreateDto createDto = new BankCreateDto();
        createDto.setName("Bank X");
        createDto.setType(BankTypeEnum.CHECKING); // use a valid enum
        createDto.setOpeningDate(LocalDate.now());

        // Mock repository to return empty to simulate "not found"
        when(bankTypeRepository.findByType(BankTypeEnum.CHECKING)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> controller.createNewBank(createDto));

        assertEquals("Bank type not found: CHECKING", exception.getMessage());
    }

}
