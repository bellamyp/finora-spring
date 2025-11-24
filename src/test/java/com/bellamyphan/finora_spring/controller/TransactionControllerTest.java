package com.bellamyphan.finora_spring.controller;

import com.bellamyphan.finora_spring.dto.TransactionResponseDto;
import com.bellamyphan.finora_spring.dto.TransactionSearchDto;
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

    @InjectMocks
    private TransactionController transactionController;

    @Test
    void testSearchTransactions_ReturnsResults() {
        TransactionSearchDto searchDto = new TransactionSearchDto();
        searchDto.setKeyword("test");

        TransactionResponseDto tx1 = new TransactionResponseDto();
        tx1.setId("tx1");
        TransactionResponseDto tx2 = new TransactionResponseDto();
        tx2.setId("tx2");

        List<TransactionResponseDto> mockedResults = Arrays.asList(tx1, tx2);
        when(transactionService.searchTransactions(any(TransactionSearchDto.class)))
                .thenReturn(mockedResults);

        ResponseEntity<List<TransactionResponseDto>> response = transactionController.searchTransactions(searchDto);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        assertEquals("tx1", response.getBody().get(0).getId());

        ArgumentCaptor<TransactionSearchDto> captor = ArgumentCaptor.forClass(TransactionSearchDto.class);
        verify(transactionService, times(1)).searchTransactions(captor.capture());
        assertEquals("test", captor.getValue().getKeyword());
    }

    @Test
    void testSearchTransactions_EmptyResults() {
        TransactionSearchDto searchDto = new TransactionSearchDto();
        when(transactionService.searchTransactions(any(TransactionSearchDto.class)))
                .thenReturn(List.of());

        ResponseEntity<List<TransactionResponseDto>> response = transactionController.searchTransactions(searchDto);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());

        verify(transactionService, times(1)).searchTransactions(any(TransactionSearchDto.class));
    }
}

