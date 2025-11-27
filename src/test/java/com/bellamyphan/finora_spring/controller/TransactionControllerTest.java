package com.bellamyphan.finora_spring.controller;

import com.bellamyphan.finora_spring.dto.TransactionResponseDto;
import com.bellamyphan.finora_spring.dto.TransactionSearchDto;
import com.bellamyphan.finora_spring.entity.User;
import com.bellamyphan.finora_spring.service.JwtService;
import com.bellamyphan.finora_spring.service.TransactionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionControllerTest {

    @Mock
    private TransactionService transactionService;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private TransactionController transactionController;

    // -------------------------------------------------
    // TEST: /search
    // -------------------------------------------------
    @Test
    void testSearchTransactions_ReturnsResults() {
        TransactionSearchDto searchDto = new TransactionSearchDto();
        searchDto.setKeyword("test");

        // mock user
        User mockUser = new User();
        mockUser.setId("user123");
        when(jwtService.getCurrentUser()).thenReturn(mockUser);

        // mock service results
        TransactionResponseDto tx1 = new TransactionResponseDto();
        tx1.setId("tx1");
        TransactionResponseDto tx2 = new TransactionResponseDto();
        tx2.setId("tx2");

        List<TransactionResponseDto> mockResults = Arrays.asList(tx1, tx2);

        when(transactionService.searchTransactions(any(TransactionSearchDto.class), eq(mockUser)))
                .thenReturn(mockResults);

        // call method
        ResponseEntity<List<TransactionResponseDto>> response =
                transactionController.searchTransactions(searchDto);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
        assertEquals("tx1", response.getBody().get(0).getId());

        // verify parameters using ArgumentCaptor
        ArgumentCaptor<TransactionSearchDto> dtoCaptor =
                ArgumentCaptor.forClass(TransactionSearchDto.class);
        ArgumentCaptor<User> userCaptor =
                ArgumentCaptor.forClass(User.class);

        verify(transactionService, times(1))
                .searchTransactions(dtoCaptor.capture(), userCaptor.capture());

        assertEquals("test", dtoCaptor.getValue().getKeyword());
        assertEquals("user123", userCaptor.getValue().getId());
    }

    @Test
    void testSearchTransactions_EmptyResults() {
        TransactionSearchDto searchDto = new TransactionSearchDto();

        User mockUser = new User();
        when(jwtService.getCurrentUser()).thenReturn(mockUser);

        when(transactionService.searchTransactions(any(TransactionSearchDto.class), eq(mockUser)))
                .thenReturn(List.of());

        ResponseEntity<List<TransactionResponseDto>> response =
                transactionController.searchTransactions(searchDto);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isEmpty());

        verify(transactionService, times(1))
                .searchTransactions(any(TransactionSearchDto.class), eq(mockUser));
    }

    // -------------------------------------------------
    // TEST: /pending
    // -------------------------------------------------
    @Test
    void testGetPendingTransactions() {
        User user = new User();
        user.setId("u123");
        when(jwtService.getCurrentUser()).thenReturn(user);

        TransactionResponseDto tx = new TransactionResponseDto();
        tx.setId("p1");

        when(transactionService.getPendingTransactionsForUser(user))
                .thenReturn(List.of(tx));

        ResponseEntity<List<TransactionResponseDto>> response =
                transactionController.getPendingTransactions();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals("p1", response.getBody().get(0).getId());

        verify(transactionService, times(1)).getPendingTransactionsForUser(user);
    }
}
