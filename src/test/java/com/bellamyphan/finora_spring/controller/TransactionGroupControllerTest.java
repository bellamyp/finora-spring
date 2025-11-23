package com.bellamyphan.finora_spring.controller;

import com.bellamyphan.finora_spring.dto.TransactionGroupCreateDto;
import com.bellamyphan.finora_spring.dto.TransactionGroupResponseDto;
import com.bellamyphan.finora_spring.entity.User;
import com.bellamyphan.finora_spring.service.TransactionGroupService;
import com.bellamyphan.finora_spring.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionGroupControllerTest {

    @Mock
    private TransactionGroupService transactionGroupService;

    @Mock
    private UserService userService;

    @InjectMocks
    private TransactionGroupController controller;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    private User mockUser;

    @BeforeEach
    void setUp() {
        mockUser = new User();
        mockUser.setId("user123");

        SecurityContextHolder.setContext(securityContext);

        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
        lenient().when(authentication.getName()).thenReturn(mockUser.getId());
        lenient().when(userService.findById(mockUser.getId())).thenReturn(Optional.of(mockUser));
    }

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
        assertThat(body.get("success")).isEqualTo(true);
        assertThat(body.get("groupId")).isEqualTo(expectedGroupId);
        assertThat(body.get("message")).isEqualTo("Transaction group created successfully");

        verify(transactionGroupService, times(1)).createTransactionGroup(dto);
        verifyNoMoreInteractions(transactionGroupService);
    }

    @Test
    void getGroupsForCurrentUser_ReturnsPostedGroups() {
        // Arrange
        TransactionGroupResponseDto groupDto = new TransactionGroupResponseDto();
        groupDto.setId("grp123");

        when(transactionGroupService.getPostedTransactionGroupsForUser(mockUser))
                .thenReturn(List.of(groupDto));

        // Act
        ResponseEntity<List<TransactionGroupResponseDto>> response =
                controller.getGroupsForCurrentUser("posted");

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsExactly(groupDto);

        verify(transactionGroupService, times(1)).getPostedTransactionGroupsForUser(mockUser);
        verifyNoMoreInteractions(transactionGroupService);
    }

    @Test
    void getGroupsForCurrentUser_ReturnsPendingGroups() {
        // Arrange
        TransactionGroupResponseDto groupDto = new TransactionGroupResponseDto();
        groupDto.setId("grp456");

        when(transactionGroupService.getPendingTransactionGroupsForUser(mockUser))
                .thenReturn(List.of(groupDto));

        // Act
        ResponseEntity<List<TransactionGroupResponseDto>> response =
                controller.getGroupsForCurrentUser("pending");

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsExactly(groupDto);

        verify(transactionGroupService, times(1)).getPendingTransactionGroupsForUser(mockUser);
        verifyNoMoreInteractions(transactionGroupService);
    }

    @Test
    void getGroupsForCurrentUser_InvalidStatus_ThrowsException() {
        // Act & Assert
        try {
            controller.getGroupsForCurrentUser("invalid");
        } catch (IllegalArgumentException ex) {
            assertThat(ex.getMessage()).contains("Invalid status: invalid");
        }

        verifyNoInteractions(transactionGroupService);
    }
}
