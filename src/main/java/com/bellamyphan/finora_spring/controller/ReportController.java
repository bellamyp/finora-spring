package com.bellamyphan.finora_spring.controller;

import com.bellamyphan.finora_spring.dto.ReportBankBalanceDto;
import com.bellamyphan.finora_spring.dto.ReportDto;
import com.bellamyphan.finora_spring.dto.ReportTypeBalanceDto;
import com.bellamyphan.finora_spring.entity.Report;
import com.bellamyphan.finora_spring.entity.ReportType;
import com.bellamyphan.finora_spring.entity.User;
import com.bellamyphan.finora_spring.service.JwtService;
import com.bellamyphan.finora_spring.service.ReportBankService;
import com.bellamyphan.finora_spring.service.ReportService;
import com.bellamyphan.finora_spring.service.ReportTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;
    private final ReportTypeService reportTypeService;
    private final ReportBankService reportBankService;
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
    // POST add fully posted transaction groups to a report
    // -----------------------
    @PostMapping("/{reportId}/add-groups")
    public ResponseEntity<Void> addTransactionGroupsToReport(@PathVariable String reportId) {
        // Get the current logged-in user from JWT token
        User user = jwtService.getCurrentUser();

        // Call service to add all fully posted groups to this report
        reportService.addTransactionGroupsToReport(user, reportId);

        return ResponseEntity.ok().build();
    }

    // -----------------------
    // POST remove a report from a transaction group
    // -----------------------
    @PostMapping("/groups/{groupId}/remove-report")
    public ResponseEntity<Void> removeReportFromGroup(@PathVariable String groupId) {
        // Get the current logged-in user from JWT token
        User user = jwtService.getCurrentUser();

        // Call service to handle the removal logic
        reportService.removeReportFromGroup(user, groupId);

        return ResponseEntity.ok().build();
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
    // GET report by ID
    // -----------------------
    @GetMapping("/{reportId}")
    public ResponseEntity<ReportDto> getReportById(@PathVariable String reportId) {
        // Get the current logged-in user from JWT token
        User user = jwtService.getCurrentUser();

        ReportDto report = reportService.getReportDtoById(user, reportId);
        return ResponseEntity.ok(report);
    }

    // -----------------------
    // GET check if user has any pending report
    // -----------------------
    @GetMapping("/has-pending")
    public ResponseEntity<Boolean> hasPendingReport() {
        // Get the current logged-in user from JWT token
        User user = jwtService.getCurrentUser();

        // Check if user has any pending (not posted) reports
        boolean hasPending = reportService.hasPendingReport(user);

        return ResponseEntity.ok(hasPending);
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

    // -----------------------
    // GET report type balances
    // -----------------------
    @GetMapping("/{reportId}/type-balances")
    public ResponseEntity<List<ReportTypeBalanceDto>> getReportTypeBalances(@PathVariable String reportId) {
        // 1️⃣ Get current user
        User user = jwtService.getCurrentUser();

        // 2️⃣ Load report and check ownership
        Report report = reportService.getReportEntityById(user, reportId);

        // 3️⃣ Get balances from ReportTypeService
        List<ReportType> balances = reportTypeService.getTypeBalances(report);

        // 4️⃣ Map to DTO
        List<ReportTypeBalanceDto> dtoList = balances.stream()
                .map(rt -> new ReportTypeBalanceDto(
                        rt.getType().getType().toString(),
                        rt.getTotalAmount()
                ))
                .toList();

        return ResponseEntity.ok(dtoList);
    }

    // -----------------------
    // GET report bank balances
    // -----------------------
    @GetMapping("/{reportId}/bank-balances")
    public ResponseEntity<List<ReportBankBalanceDto>> getReportBankBalances(@PathVariable String reportId) {
        // 1️⃣ Get current user
        User user = jwtService.getCurrentUser();

        // 2️⃣ Load report and check ownership
        Report report = reportService.getReportEntityById(user, reportId);

        // 3️⃣ Get balances from ReportBankService
        List<ReportBankBalanceDto> dtoList = reportBankService.getBankBalances(report);

        return ResponseEntity.ok(dtoList);
    }
}
