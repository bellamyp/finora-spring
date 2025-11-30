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

    @Transactional
    public ReportDto createNewReport(User user) {

        LocalDate nextMonth;

        // 1️⃣ Check if user has existing reports
        Optional<Report> lastReportOpt = reportRepository.findTopByUserIdOrderByMonthDesc(user.getId());
        List<TransactionGroupResponseDto> fullyPostedGroups = null;

        if (lastReportOpt.isPresent()) {
            // Next month after latest report
            nextMonth = lastReportOpt.get().getMonth().plusMonths(1).withDayOfMonth(1);
        } else {
            // 2️⃣ Else, use earliest fully posted transaction group's month
            fullyPostedGroups = transactionGroupService.getFullyPostedGroupsForNewReport(user);

            if (!fullyPostedGroups.isEmpty()) {
                // Earliest transaction in the earliest group
                LocalDate earliestDate = fullyPostedGroups.stream()
                        .flatMap(g -> g.getTransactions().stream())
                        .map(tx -> tx.getDate() != null ? LocalDate.parse(tx.getDate()) : null)
                        .filter(Objects::nonNull)
                        .min(LocalDate::compareTo)
                        .orElse(LocalDate.now());
                nextMonth = earliestDate.withDayOfMonth(1);
            } else {
                // 3️⃣ Else, use current month
                nextMonth = LocalDate.now().withDayOfMonth(1);
            }
        }

        // 4️⃣ Create and save the report entity
        String id = nanoIdService.generateUniqueId(reportRepository);
        Report report = new Report(id, nextMonth, user, false);
        reportRepository.save(report);

        // 5️⃣ Assign fully posted groups to this report (reuse list if available)
        if (fullyPostedGroups != null && !fullyPostedGroups.isEmpty()) {
            List<TransactionGroup> groupsToInclude = transactionGroupRepository
                    .findAllById(fullyPostedGroups.stream()
                            .map(TransactionGroupResponseDto::getId)
                            .toList());
            groupsToInclude.forEach(g -> g.setReport(report));
            transactionGroupRepository.saveAll(groupsToInclude);
        }

        // 5️⃣ Bulk assign fully posted groups to this report
        if (fullyPostedGroups != null && !fullyPostedGroups.isEmpty()) {
            List<String> groupIds = fullyPostedGroups.stream()
                    .map(TransactionGroupResponseDto::getId)
                    .toList();
            transactionGroupRepository.assignGroupsToReport(report, groupIds);
        }

        // 6️⃣ Map to DTO
        ReportDto dto = new ReportDto();
        dto.setId(report.getId());
        dto.setUserId(user.getId());
        dto.setMonth(report.getMonth());
        dto.setPosted(report.isPosted());

        return dto;
    }
}
