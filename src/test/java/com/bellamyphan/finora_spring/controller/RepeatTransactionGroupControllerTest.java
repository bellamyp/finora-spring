package com.bellamyphan.finora_spring.controller;

import com.bellamyphan.finora_spring.entity.RepeatTransactionGroup;
import com.bellamyphan.finora_spring.entity.Transaction;
import com.bellamyphan.finora_spring.entity.TransactionGroup;
import com.bellamyphan.finora_spring.entity.User;
import com.bellamyphan.finora_spring.service.RepeatTransactionGroupService;
import com.bellamyphan.finora_spring.service.TransactionGroupService;
import com.bellamyphan.finora_spring.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RepeatTransactionGroupControllerTest {

    @Mock
    private RepeatTransactionGroupService repeatService;

    @Mock
    private TransactionGroupService groupService;

    @Mock
    private UserService userService;

    @InjectMocks
    private RepeatTransactionGroupController controller;

    private User mockUser;
    private TransactionGroup mockGroup;

    private Transaction mockTxn() {
        Transaction t = new Transaction();
        t.setId("T1");
        return t;
    }

    @BeforeEach
    void setup() {
        mockUser = new User();
        mockUser.setId("user123");

        mockGroup = new TransactionGroup();
        mockGroup.setId("G1");

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("user123", null)
        );
    }

    // ---------------------------------------------------------
    // GET /is-repeat
    // ---------------------------------------------------------

    @Test
    void isRepeat_returnsForbidden_whenUserHasNoOwnership() {
        when(userService.findById("user123")).thenReturn(Optional.of(mockUser));
        when(groupService.fetchTransactionGroup("G1")).thenReturn(mockGroup);
        when(groupService.getUserTransactionsForGroup(mockGroup, mockUser)).thenReturn(List.of());

        ResponseEntity<?> response = controller.isRepeat("G1");

        assertEquals(403, response.getStatusCode().value());
        assertEquals("You are not allowed to access this group's repeat status", response.getBody());
    }

    @Test
    void isRepeat_returnsOk_whenUserOwnsGroup() {
        when(userService.findById("user123")).thenReturn(Optional.of(mockUser));
        when(groupService.fetchTransactionGroup("G1")).thenReturn(mockGroup);
        when(groupService.getUserTransactionsForGroup(mockGroup, mockUser))
                .thenReturn(List.of(mockTxn()));

        when(repeatService.exists("G1")).thenReturn(true);

        ResponseEntity<?> response = controller.isRepeat("G1");

        assertEquals(200, response.getStatusCode().value());
        assertEquals(true, response.getBody());
    }

    // ---------------------------------------------------------
    // POST /{groupId}
    // ---------------------------------------------------------

    @Test
    void markAsRepeat_returnsForbidden_whenNoOwnership() {
        when(userService.findById("user123")).thenReturn(Optional.of(mockUser));
        when(groupService.fetchTransactionGroup("G1")).thenReturn(mockGroup);
        when(groupService.getUserTransactionsForGroup(mockGroup, mockUser)).thenReturn(List.of());

        ResponseEntity<?> response = controller.markAsRepeat("G1");

        assertEquals(403, response.getStatusCode().value());
        assertEquals("You are not allowed to mark this empty group as repeat", response.getBody());
    }

    @Test
    void markAsRepeat_returnsOk_whenOwnershipValid() {
        when(userService.findById("user123")).thenReturn(Optional.of(mockUser));
        when(groupService.fetchTransactionGroup("G1")).thenReturn(mockGroup);
        when(groupService.getUserTransactionsForGroup(mockGroup, mockUser))
                .thenReturn(List.of(mockTxn()));

        RepeatTransactionGroup repeat = new RepeatTransactionGroup(mockGroup);
        when(repeatService.markAsRepeat(mockGroup)).thenReturn(repeat);

        ResponseEntity<?> response = controller.markAsRepeat("G1");

        assertEquals(200, response.getStatusCode().value());
        assertSame(repeat, response.getBody());
    }

    // ---------------------------------------------------------
    // DELETE /{groupId}
    // ---------------------------------------------------------

    @Test
    void removeRepeat_returnsForbidden_whenNoOwnership() {
        when(userService.findById("user123")).thenReturn(Optional.of(mockUser));
        when(groupService.fetchTransactionGroup("G1")).thenReturn(mockGroup);
        when(groupService.getUserTransactionsForGroup(mockGroup, mockUser)).thenReturn(List.of());

        ResponseEntity<?> response = controller.removeRepeat("G1");

        assertEquals(403, response.getStatusCode().value());
        assertEquals("You are not allowed to remove repeat status from this group", response.getBody());
    }

    @Test
    void removeRepeat_returnsNotFound_whenNotExists() {
        when(userService.findById("user123")).thenReturn(Optional.of(mockUser));
        when(groupService.fetchTransactionGroup("G1")).thenReturn(mockGroup);
        when(groupService.getUserTransactionsForGroup(mockGroup, mockUser))
                .thenReturn(List.of(mockTxn()));

        when(repeatService.exists("G1")).thenReturn(false);

        ResponseEntity<?> response = controller.removeRepeat("G1");

        assertEquals(404, response.getStatusCode().value());
        assertEquals("Group is not marked as repeat", response.getBody());
    }

    @Test
    void removeRepeat_returnsOk_whenExists() {
        when(userService.findById("user123")).thenReturn(Optional.of(mockUser));
        when(groupService.fetchTransactionGroup("G1")).thenReturn(mockGroup);
        when(groupService.getUserTransactionsForGroup(mockGroup, mockUser))
                .thenReturn(List.of(mockTxn()));

        when(repeatService.exists("G1")).thenReturn(true);

        ResponseEntity<?> response = controller.removeRepeat("G1");

        assertEquals(200, response.getStatusCode().value());
        assertEquals("Repeat status removed successfully", response.getBody());
        verify(repeatService).removeRepeat(mockGroup);
    }
}
