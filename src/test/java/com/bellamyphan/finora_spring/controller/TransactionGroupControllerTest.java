package com.bellamyphan.finora_spring.controller;

import com.bellamyphan.finora_spring.dto.TransactionGroupCreateDto;
import com.bellamyphan.finora_spring.service.TransactionGroupService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionGroupControllerTest {

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
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertThat(body).isNotNull();
        Assertions.assertNotNull(body);
        assertThat(body.get("success")).isEqualTo(true);
        assertThat(body.get("groupId")).isEqualTo(expectedGroupId);
        assertThat(body.get("message")).isEqualTo("Transaction group created successfully");

        verify(transactionGroupService, times(1)).createTransactionGroup(dto);
        verifyNoMoreInteractions(transactionGroupService);
    }
}