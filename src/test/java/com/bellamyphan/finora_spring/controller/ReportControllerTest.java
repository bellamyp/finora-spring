package com.bellamyphan.finora_spring.controller;

import com.bellamyphan.finora_spring.constant.RoleEnum;
import com.bellamyphan.finora_spring.dto.ReportDto;
import com.bellamyphan.finora_spring.entity.Role;
import com.bellamyphan.finora_spring.entity.User;
import com.bellamyphan.finora_spring.service.JwtService;
import com.bellamyphan.finora_spring.service.ReportService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
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
        // Sample Role
        Role role = new Role();
        role.setId("role1");
        role.setName(RoleEnum.ROLE_USER);

        // Sample User
        mockUser = new User("Test User", "test@example.com", "password123", role);
        mockUser.setId("user123");

        // Sample ReportDto
        mockReport = new ReportDto();
        mockReport.setId("report123");
        mockReport.setUserId(mockUser.getId());
        mockReport.setMonth(LocalDate.of(2025, 11, 1));
        mockReport.setPosted(false);
    }

    @Test
    void createNewReport_ShouldReturnCreatedReport() {
        // Arrange
        when(jwtService.getCurrentUser()).thenReturn(mockUser);
        when(reportService.createNewReport(mockUser)).thenReturn(mockReport);

        // Act
        ResponseEntity<ReportDto> response = reportController.createNewReport();

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode()); // Use enum instead of deprecated method
        assertEquals(mockReport, response.getBody());

        // Verify interactions
        verify(jwtService, times(1)).getCurrentUser();
        verify(reportService, times(1)).createNewReport(mockUser);
    }
}
