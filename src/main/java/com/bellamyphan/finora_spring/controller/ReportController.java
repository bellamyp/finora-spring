package com.bellamyphan.finora_spring.controller;

import com.bellamyphan.finora_spring.dto.ReportDto;
import com.bellamyphan.finora_spring.entity.User;
import com.bellamyphan.finora_spring.service.JwtService;
import com.bellamyphan.finora_spring.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;
    private final JwtService jwtService;

    // -----------------------
    // POST create new report
    // -----------------------
    @PostMapping("/new")
    public ResponseEntity<ReportDto> createNewReport() {
        // Get the current logged-in user from JWT token
        User user = jwtService.getCurrentUser();

        // Call service to create the next report
        ReportDto report = reportService.createNewReport(user);

        // Return report directly with CREATED status
        return new ResponseEntity<>(report, HttpStatus.CREATED);
    }


    // -----------------------
    // GET all reports
    // -----------------------
    @GetMapping
    public ResponseEntity<List<ReportDto>> getAllReports() {
        // Get the current logged-in user from JWT token
        User user = jwtService.getCurrentUser();

        List<ReportDto> reportDtos = reportService.getAllReportsByUser(user);
        return ResponseEntity.ok(reportDtos);
    }

    // -----------------------
    // GET check if user can generate new report
    // -----------------------
    @GetMapping("/can-generate")
    public ResponseEntity<Boolean> canGenerateNewReport() {
        // Get the current logged-in user from JWT token
        User user = jwtService.getCurrentUser();

        // Check if user has any pending (not posted) reports
        boolean hasPending = reportService.hasPendingReport(user);

        // Can generate a new report if there are no pending reports
        return ResponseEntity.ok(!hasPending);
    }

    // -----------------------
    // GET check if user can add transaction groups
    // -----------------------
    @GetMapping("/can-add-groups")
    public ResponseEntity<Boolean> canAddTransactionGroups() {
        // Get the current logged-in user from JWT token
        User user = jwtService.getCurrentUser();

        boolean canAdd = reportService.canAddTransactionGroups(user);
        return ResponseEntity.ok(canAdd);
    }
}
