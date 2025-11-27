package com.bellamyphan.finora_spring.controller;

import com.bellamyphan.finora_spring.dto.TransactionGroupCreateDto;
import com.bellamyphan.finora_spring.dto.TransactionGroupResponseDto;
import com.bellamyphan.finora_spring.entity.User;
import com.bellamyphan.finora_spring.service.JwtService;
import com.bellamyphan.finora_spring.service.TransactionGroupService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

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
    private JwtService jwtService;

    @InjectMocks
    private TransactionGroupController controller;

    private User mockUser;

    @BeforeEach
    void setUp() {
        mockUser = new User();
        mockUser.setId("user123");

        lenient().when(jwtService.getCurrentUser()).thenReturn(mockUser);
    }

    @Test
    void createTransactionGroup_ReturnsSuccessResponse() {
        TransactionGroupCreateDto dto = new TransactionGroupCreateDto();
        String expectedGroupId = "abc123";

        when(transactionGroupService.createTransactionGroup(dto))
                .thenReturn(expectedGroupId);

        ResponseEntity<?> response = controller.createTransactionGroup(dto);

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
        TransactionGroupResponseDto groupDto = new TransactionGroupResponseDto();
        groupDto.setId("grp123");

        when(transactionGroupService.getPostedTransactionGroupsForUser(mockUser))
                .thenReturn(List.of(groupDto));

        ResponseEntity<List<TransactionGroupResponseDto>> response =
                controller.getGroupsForCurrentUser("posted");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsExactly(groupDto);

        verify(transactionGroupService, times(1)).getPostedTransactionGroupsForUser(mockUser);
        verifyNoMoreInteractions(transactionGroupService);
    }

    @Test
    void getGroupsForCurrentUser_ReturnsPendingGroups() {
        TransactionGroupResponseDto groupDto = new TransactionGroupResponseDto();
        groupDto.setId("grp456");

        when(transactionGroupService.getPendingTransactionGroupsForUser(mockUser))
                .thenReturn(List.of(groupDto));

        ResponseEntity<List<TransactionGroupResponseDto>> response =
                controller.getGroupsForCurrentUser("pending");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsExactly(groupDto);

        verify(transactionGroupService, times(1)).getPendingTransactionGroupsForUser(mockUser);
        verifyNoMoreInteractions(transactionGroupService);
    }

    @Test
    void getGroupsForCurrentUser_ReturnsRepeatGroups() {
        TransactionGroupResponseDto groupDto = new TransactionGroupResponseDto();
        groupDto.setId("grpRepeat");

        when(transactionGroupService.getRepeatTransactionGroupsForUser(mockUser))
                .thenReturn(List.of(groupDto));

        ResponseEntity<List<TransactionGroupResponseDto>> response =
                controller.getGroupsForCurrentUser("repeat");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsExactly(groupDto);

        verify(transactionGroupService, times(1)).getRepeatTransactionGroupsForUser(mockUser);
        verifyNoMoreInteractions(transactionGroupService);
    }

    @Test
    void getGroupsForCurrentUser_InvalidStatus_ThrowsException() {
        try {
            controller.getGroupsForCurrentUser("invalid");
        } catch (IllegalArgumentException ex) {
            assertThat(ex.getMessage()).contains("Invalid status: invalid");
        }

        verifyNoInteractions(transactionGroupService);
    }

    @Test
    void getGroupById_ReturnsGroup() {
        TransactionGroupResponseDto groupDto = new TransactionGroupResponseDto();
        groupDto.setId("grp789");

        when(transactionGroupService.getTransactionGroupByIdForUser("grp789", mockUser))
                .thenReturn(Optional.of(groupDto));

        ResponseEntity<TransactionGroupResponseDto> response =
                controller.getGroupById("grp789");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(groupDto);

        verify(transactionGroupService, times(1))
                .getTransactionGroupByIdForUser("grp789", mockUser);
        verifyNoMoreInteractions(transactionGroupService);
    }

    @Test
    void updateTransactionGroup_ReturnsSuccess() {
        TransactionGroupResponseDto dto = new TransactionGroupResponseDto();
        dto.setId("grpUpdate");

        ResponseEntity<?> response = controller.updateTransactionGroup(dto);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.get("success")).isEqualTo(true);
        assertThat(body.get("message")).isEqualTo("Transaction group updated successfully");

        verify(transactionGroupService, times(1)).updateTransactionGroup(dto, mockUser);
    }

    @Test
    void updateTransactionGroup_ReturnsBadRequest_WhenIdMissing() {
        TransactionGroupResponseDto dto = new TransactionGroupResponseDto();
        dto.setId(null);

        // No need to stub jwtService.getCurrentUser(), controller returns early
        ResponseEntity<?> response = controller.updateTransactionGroup(dto);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.get("success")).isEqualTo(false);
        assertThat(body.get("message")).isEqualTo("Group ID must be provided for update");

        verifyNoInteractions(transactionGroupService);
    }

    @Test
    void updateTransactionGroup_ReturnsServerError_OnException() {
        TransactionGroupResponseDto dto = new TransactionGroupResponseDto();
        dto.setId("grpError");

        doThrow(new RuntimeException("DB failure"))
                .when(transactionGroupService).updateTransactionGroup(dto, mockUser);

        ResponseEntity<?> response = controller.updateTransactionGroup(dto);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.get("success")).isEqualTo(false);
        assertThat(((String) body.get("message"))).contains("Failed to update transaction group: DB failure");

        verify(transactionGroupService, times(1)).updateTransactionGroup(dto, mockUser);
    }
}
