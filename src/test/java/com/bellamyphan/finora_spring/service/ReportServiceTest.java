package com.bellamyphan.finora_spring.service;

import com.bellamyphan.finora_spring.constant.RoleEnum;
import com.bellamyphan.finora_spring.dto.ReportDto;
import com.bellamyphan.finora_spring.dto.TransactionGroupResponseDto;
import com.bellamyphan.finora_spring.dto.TransactionResponseDto;
import com.bellamyphan.finora_spring.entity.Report;
import com.bellamyphan.finora_spring.entity.TransactionGroup;
import com.bellamyphan.finora_spring.entity.User;
import com.bellamyphan.finora_spring.entity.Role;
import com.bellamyphan.finora_spring.repository.ReportRepository;
import com.bellamyphan.finora_spring.repository.TransactionGroupRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReportServiceTest {

    @Mock
    private ReportRepository reportRepository;

    @Mock
    private TransactionGroupRepository transactionGroupRepository;

    @Mock
    private TransactionGroupService transactionGroupService;

    @Mock
    private NanoIdService nanoIdService;

    @InjectMocks
    private ReportService reportService;

    private User testUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Create a dummy Role (required by User)
        Role role = new Role();
        role.setId("role1");
        role.setName(RoleEnum.ROLE_USER);

        testUser = new User("Test User", "test@example.com", "password123", role);
        testUser.setId("user123");
    }

    @Test
    void createNewReport_WhenNoPreviousReportAndNoGroups_ShouldUseCurrentMonth() {
        // Arrange
        when(reportRepository.findTopByUserIdOrderByMonthDesc(testUser.getId()))
                .thenReturn(Optional.empty());
        when(transactionGroupService.getFullyPostedGroupsForNewReport(testUser))
                .thenReturn(List.of());
        when(nanoIdService.generateUniqueId(reportRepository)).thenReturn("report123");

        // Act
        ReportDto dto = reportService.createNewReport(testUser);

        // Assert
        assertNotNull(dto);
        assertEquals("report123", dto.getId());
        assertEquals(testUser.getId(), dto.getUserId());
        assertEquals(LocalDate.now().withDayOfMonth(1), dto.getMonth());
        assertFalse(dto.isPosted());

        verify(reportRepository, times(1)).save(any(Report.class));
        verify(reportRepository, times(1)).flush();
        verify(transactionGroupRepository, never()).saveAll(anyList());
    }

    @Test
    void createNewReport_WhenPreviousReportExists_ShouldUseNextMonth() {
        // Arrange
        Report lastReport = new Report("r1", LocalDate.of(2025, 10, 1), testUser, true);
        when(reportRepository.findTopByUserIdOrderByMonthDesc(testUser.getId()))
                .thenReturn(Optional.of(lastReport));
        when(transactionGroupService.getFullyPostedGroupsForNewReport(testUser))
                .thenReturn(List.of());
        when(nanoIdService.generateUniqueId(reportRepository)).thenReturn("report456");

        // Act
        ReportDto dto = reportService.createNewReport(testUser);

        // Assert
        assertEquals(LocalDate.of(2025, 11, 1), dto.getMonth());
        assertEquals("report456", dto.getId());
    }

    @Test
    void createNewReport_WhenFullyPostedGroupsExist_ShouldAssignGroups() {
        // Arrange
        TransactionResponseDto tx1 = new TransactionResponseDto();
        tx1.setDate("2025-09-15");

        TransactionGroupResponseDto group = new TransactionGroupResponseDto();
        group.setId("g1");
        group.setTransactions(List.of(tx1));

        when(reportRepository.findTopByUserIdOrderByMonthDesc(testUser.getId()))
                .thenReturn(Optional.empty());
        when(transactionGroupService.getFullyPostedGroupsForNewReport(testUser))
                .thenReturn(List.of(group));
        when(transactionGroupRepository.findAllById(List.of("g1")))
                .thenReturn(List.of(new TransactionGroup()));
        when(nanoIdService.generateUniqueId(reportRepository)).thenReturn("report789");

        // Act
        ReportDto dto = reportService.createNewReport(testUser);

        // Assert
        assertEquals(LocalDate.of(2025, 9, 1), dto.getMonth());
        verify(transactionGroupRepository, times(1)).saveAll(anyList());
    }
}
