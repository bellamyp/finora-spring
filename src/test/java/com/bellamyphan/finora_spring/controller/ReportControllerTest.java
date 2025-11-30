package com.bellamyphan.finora_spring.controller;

import com.bellamyphan.finora_spring.constant.RoleEnum;
import com.bellamyphan.finora_spring.dto.ReportDto;
import com.bellamyphan.finora_spring.entity.Role;
import com.bellamyphan.finora_spring.entity.User;
import com.bellamyphan.finora_spring.service.JwtService;
import com.bellamyphan.finora_spring.service.ReportService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class ReportControllerTest {

    @Mock
    private ReportService reportService;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private ReportController reportController;

    private User mockUser;
    private ReportDto mockReport;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Create a dummy Role (needed by User)
        Role role = new Role();
        role.setId("role1");
        role.setName(RoleEnum.ROLE_USER);

        // Sample user with all required fields
        mockUser = new User("Test User", "test@example.com", "password123", role);
        mockUser.setId("user123"); // set ID manually for testing

        // Sample report DTO
        mockReport = new ReportDto();
        mockReport.setId("report123");
        mockReport.setUserId(mockUser.getId());
        mockReport.setMonth(LocalDate.of(2025, 11, 1));
        mockReport.setPosted(false);
    }

    @Test
    void createNewReport_ShouldReturnCreatedReport() {
        // Arrange: mock service calls
        when(jwtService.getCurrentUser()).thenReturn(mockUser);
        when(reportService.createNewReport(mockUser)).thenReturn(mockReport);

        // Act
        ResponseEntity<ReportDto> response = reportController.createNewReport();

        // Assert
        assertEquals(201, response.getStatusCodeValue()); // HTTP CREATED
        assertEquals(mockReport, response.getBody());

        // Verify interactions
        verify(jwtService, times(1)).getCurrentUser();
        verify(reportService, times(1)).createNewReport(mockUser);
    }
}
