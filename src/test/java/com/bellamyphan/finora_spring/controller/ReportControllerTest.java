package com.bellamyphan.finora_spring.controller;

import com.bellamyphan.finora_spring.constant.RoleEnum;
import com.bellamyphan.finora_spring.dto.ReportDto;
import com.bellamyphan.finora_spring.entity.Report;
import com.bellamyphan.finora_spring.entity.Role;
import com.bellamyphan.finora_spring.entity.User;
import com.bellamyphan.finora_spring.service.JwtService;
import com.bellamyphan.finora_spring.service.ReportBankService;
import com.bellamyphan.finora_spring.service.ReportService;
import com.bellamyphan.finora_spring.service.ReportTypeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReportControllerTest {

    @Mock
    private ReportService reportService;

    @Mock
    private ReportTypeService reportTypeService;

    @Mock
    private ReportBankService reportBankService;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private ReportController reportController;

    private User mockUser;
    private ReportDto mockReport;
    private Report mockReportEntity;

    @BeforeEach
    void setUp() {
        Role role = new Role();
        role.setId("role1");
        role.setName(RoleEnum.ROLE_USER);

        mockUser = new User("Test User", "test@example.com", "password123", role);
        mockUser.setId("user123");

        mockReport = new ReportDto();
        mockReport.setId("report123");
        mockReport.setUserId(mockUser.getId());
        mockReport.setMonth(LocalDate.of(2025, 11, 1));
        mockReport.setPosted(false);

        mockReportEntity = new Report();
        mockReportEntity.setId("report123");
        mockReportEntity.setUser(mockUser);
    }

    @Test
    void createNewReport_ShouldReturnCreatedReport() {
        when(jwtService.getCurrentUser()).thenReturn(mockUser);
        when(reportService.createNewReport(mockUser)).thenReturn(mockReport);

        ResponseEntity<ReportDto> response = reportController.createNewReport();

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(mockReport, response.getBody());
        verify(jwtService, times(1)).getCurrentUser();
        verify(reportService, times(1)).createNewReport(mockUser);
    }

    @Test
    void addTransactionGroupsToReport_ShouldReturnOk() {
        when(jwtService.getCurrentUser()).thenReturn(mockUser);

        ResponseEntity<Void> response = reportController.addTransactionGroupsToReport("report123");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(reportService, times(1)).addTransactionGroupsToReport(mockUser, "report123");
    }

    @Test
    void removeReportFromGroup_ShouldReturnOk() {
        when(jwtService.getCurrentUser()).thenReturn(mockUser);

        ResponseEntity<Void> response = reportController.removeReportFromGroup("group123");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(reportService, times(1)).removeReportFromGroup(mockUser, "group123");
    }

    @Test
    void getAllReports_ShouldReturnList() {
        when(jwtService.getCurrentUser()).thenReturn(mockUser);
        when(reportService.getAllReportsByUser(mockUser)).thenReturn(List.of(mockReport));

        ResponseEntity<List<ReportDto>> response = reportController.getAllReports();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals(mockReport, response.getBody().get(0));
    }

    @Test
    void getReportById_ShouldReturnReport() {
        when(jwtService.getCurrentUser()).thenReturn(mockUser);
        when(reportService.getReportDtoById(mockUser, "report123")).thenReturn(mockReport);

        ResponseEntity<ReportDto> response = reportController.getReportById("report123");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockReport, response.getBody());
    }

    @Test
    void canGenerateNewReport_ShouldReturnBoolean() {
        when(jwtService.getCurrentUser()).thenReturn(mockUser);
        when(reportService.hasPendingReport(mockUser)).thenReturn(true);

        ResponseEntity<Boolean> response = reportController.canGenerateNewReport();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(false, response.getBody()); // negated in controller logic
    }

    @Test
    void canAddTransactionGroups_ShouldReturnBoolean() {
        when(jwtService.getCurrentUser()).thenReturn(mockUser);
        when(reportService.canAddTransactionGroups(mockUser)).thenReturn(true);

        ResponseEntity<Boolean> response = reportController.canAddTransactionGroups();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(true, response.getBody());
    }
}