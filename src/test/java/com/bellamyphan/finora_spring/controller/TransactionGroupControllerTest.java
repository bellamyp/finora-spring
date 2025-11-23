package com.bellamyphan.finora_spring.controller;

import com.bellamyphan.finora_spring.dto.TransactionGroupCreateDto;
import com.bellamyphan.finora_spring.service.TransactionGroupService;
import com.bellamyphan.finora_spring.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionGroupControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private TransactionGroupService transactionGroupService;

    @InjectMocks
    private TransactionGroupController controller;

    @Test
    void createTransactionGroup_ReturnsSuccessResponse() {
        // Arrange
        TransactionGroupCreateDto dto = new TransactionGroupCreateDto();
        String expectedGroupId = "abc123";

        when(transactionGroupService.createTransactionGroup(dto))
                .thenReturn(expectedGroupId);

        // Act
        ResponseEntity<?> response = controller.createTransactionGroup(dto);

        // Assert
        assertEquals(200, response.getStatusCodeValue());

        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertNotNull(body);
        assertTrue((Boolean) body.get("success"));
        assertEquals(expectedGroupId, body.get("groupId"));
        assertEquals("Transaction group created successfully", body.get("message"));

        verify(transactionGroupService, times(1)).createTransactionGroup(dto);
        verifyNoMoreInteractions(transactionGroupService);
    }
}