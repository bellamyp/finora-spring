package com.bellamyphan.finora_spring.service;

import com.bellamyphan.finora_spring.dto.ReportDto;
import com.bellamyphan.finora_spring.dto.TransactionGroupResponseDto;
import com.bellamyphan.finora_spring.entity.Report;
import com.bellamyphan.finora_spring.entity.TransactionGroup;
import com.bellamyphan.finora_spring.entity.User;
import com.bellamyphan.finora_spring.repository.ReportRepository;
import com.bellamyphan.finora_spring.repository.TransactionGroupRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;
    private final TransactionGroupRepository transactionGroupRepository;
    private final TransactionGroupService transactionGroupService;
    private final NanoIdService nanoIdService;

    public List<ReportDto> getAllReportsByUser(User user) {

        return reportRepository
                .findAllByUserIdOrderByMonthDesc(user.getId())
                .stream()
                .map(report -> {
                    ReportDto dto = new ReportDto();
                    dto.setId(report.getId());
                    dto.setUserId(user.getId());
                    dto.setMonth(report.getMonth());
                    dto.setPosted(report.isPosted());
                    return dto;
                })
                .toList();
    }

    public ReportDto getReportById(User user, String reportId) {
        // 1️⃣ Load the report by ID
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("Report not found: " + reportId));

        // 2️⃣ Ensure the report belongs to the current user
        if (!report.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized access to report");
        }

        // 3️⃣ Map to DTO
        ReportDto dto = new ReportDto();
        dto.setId(report.getId());
        dto.setUserId(report.getUser().getId());
        dto.setMonth(report.getMonth());
        dto.setPosted(report.isPosted());

        return dto;
    }

    /**
     * Returns true if user has at least 1 pending (not posted) report
     */
    public boolean hasPendingReport(User user) {
        return reportRepository.existsByUserIdAndIsPostedFalse(user.getId());
    }

    /**
     * Returns true if user has at least one fully-posted transaction group
     * that can be added to a report
     */
    public boolean canAddTransactionGroups(User user) {
        return !transactionGroupService
                .getFullyPostedGroupsForNewReport(user)
                .isEmpty();
    }

    @Transactional
    public ReportDto createNewReport(User user) {

        LocalDate nextMonth;

        // 1️⃣ Check if user has existing reports
        Optional<Report> lastReportOpt = reportRepository.findTopByUserIdOrderByMonthDesc(user.getId());

        // 2️⃣ Fetch fully posted groups for new report (always!)
        List<TransactionGroupResponseDto> fullyPostedGroups = transactionGroupService
                .getFullyPostedGroupsForNewReport(user);

        if (lastReportOpt.isPresent()) {
            // Next month after latest report
            nextMonth = lastReportOpt.get().getMonth().plusMonths(1).withDayOfMonth(1);
        } else if (!fullyPostedGroups.isEmpty()) {
            // Earliest transaction in the earliest group
            LocalDate earliestDate = fullyPostedGroups.stream()
                    .flatMap(g -> g.getTransactions().stream())
                    .map(tx -> tx.getDate() != null ? LocalDate.parse(tx.getDate()) : null)
                    .filter(Objects::nonNull)
                    .min(LocalDate::compareTo)
                    .orElse(LocalDate.now());
            nextMonth = earliestDate.withDayOfMonth(1);
        } else {
            // Default to current month
            nextMonth = LocalDate.now().withDayOfMonth(1);
        }

        // 3️⃣ Create and save the report
        String id = nanoIdService.generateUniqueId(reportRepository);
        Report report = new Report(id, nextMonth, user, false);
        reportRepository.save(report);
        reportRepository.flush(); // ensure report exists in DB

        // 4️⃣ Assign fully posted groups (if any)
        if (!fullyPostedGroups.isEmpty()) {
            List<String> groupIds = fullyPostedGroups.stream()
                    .map(TransactionGroupResponseDto::getId)
                    .toList();

            List<TransactionGroup> groups = transactionGroupRepository.findAllById(groupIds);
            groups.forEach(tg -> tg.setReport(report));
            transactionGroupRepository.saveAll(groups);
        }

        // 5️⃣ Map to DTO
        ReportDto dto = new ReportDto();
        dto.setId(report.getId());
        dto.setUserId(user.getId());
        dto.setMonth(report.getMonth());
        dto.setPosted(report.isPosted());

        return dto;
    }

    @Transactional
    public void addTransactionGroupsToReport(User user, String reportId) {
        // 1️⃣ Get the target report
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("Report not found: " + reportId));

        // 2️⃣ Make sure the report belongs to the user
        if (!report.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized access to report");
        }

        // 3️⃣ Get all fully posted transaction groups for this user that are not yet assigned to a report
        List<TransactionGroupResponseDto> fullyPostedGroups = transactionGroupService
                .getFullyPostedGroupsForNewReport(user);

        if (fullyPostedGroups.isEmpty()) {
            return; // nothing to add
        }

        // 4️⃣ Fetch entities by IDs
        List<String> groupIds = fullyPostedGroups.stream()
                .map(TransactionGroupResponseDto::getId)
                .toList();

        List<TransactionGroup> groups = transactionGroupRepository.findAllById(groupIds);

        // 5️⃣ Assign report to each group
        groups.forEach(tg -> tg.setReport(report));

        // 6️⃣ Save all updated groups
        transactionGroupRepository.saveAll(groups);
    }

    @Transactional
    public void removeReportFromGroup(User user, String groupId) {
        // 1️⃣ Load the transaction group
        TransactionGroup group = transactionGroupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Transaction group not found: " + groupId));

        // 2️⃣ Check if the group has a report assigned
        Report report = group.getReport();
        if (report == null) {
            throw new RuntimeException("Transaction group is not assigned to any report");
        }

        // 3️⃣ Check ownership
        if (!report.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized: Cannot modify this report");
        }

        // 4️⃣ Check if the report is posted
        if (report.isPosted()) {
            throw new RuntimeException("Cannot remove report: Report has already been posted");
        }

        // 5️⃣ Remove report from the group
        group.setReport(null);
        transactionGroupRepository.save(group);
    }
}
