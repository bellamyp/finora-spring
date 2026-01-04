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

    public List<ReportDto> getAllReportByUser(User user) {

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
}
