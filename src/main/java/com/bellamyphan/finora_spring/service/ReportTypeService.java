package com.bellamyphan.finora_spring.service;

import com.bellamyphan.finora_spring.entity.Report;
import com.bellamyphan.finora_spring.entity.ReportType;
import com.bellamyphan.finora_spring.repository.ReportRepository;
import com.bellamyphan.finora_spring.repository.ReportTypeAggregate;
import com.bellamyphan.finora_spring.repository.ReportTypeRepository;
import com.bellamyphan.finora_spring.repository.TransactionTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportTypeService {

    private final ReportRepository reportRepository;
    private final ReportTypeRepository reportTypeRepository;
    private final TransactionTypeRepository transactionTypeRepository;

    @Transactional
    public void saveTypeBalances(Report report) {

        if (report.isPosted()) {
            throw new IllegalStateException("Cannot save type balances for a posted report");
        }

        // 1️⃣ Calculate live balances
        List<ReportType> reportTypes = calculateLiveTypeBalances(report);

        // 2️⃣ Persist snapshot
        reportTypeRepository.saveAll(reportTypes);
    }

    /**
     * Returns the list of ReportType for a given report.
     * - If report is POSTED → read snapshot from DB
     * - If report is PENDING → calculate LIVE from transactions
     * NO DB writes happen here.
     */
    @Transactional(readOnly = true)
    public List<ReportType> getTypeBalances(Report report) {

        if (report.isPosted()) {
            // Snapshot for posted reports
            return reportTypeRepository.findByReportId(report.getId())
                    .stream()
                    .sorted((a, b) ->
                            a.getType().getType().toString().compareToIgnoreCase(b.getType().getType().toString()))
                    .toList();
        }

        // Live calculation for pending reports
        return calculateLiveTypeBalances(report);
    }

    /**
     * Calculate live type balances from transactions.
     * Returns one ReportType per type (multiple types per report supported)
     * These entities are not saved — only for returning to controller/DTO.
     */
    private List<ReportType> calculateLiveTypeBalances(Report report) {

        List<ReportTypeAggregate> aggregates =
                reportRepository.calculateLiveTypeBalances(report.getId());

        return aggregates.stream()
                .map(a -> new ReportType(
                        report,
                        transactionTypeRepository.getReferenceById(a.getTypeId()),
                        a.getTotalAmount()
                ))
                .sorted((r1, r2) ->
                        r1.getType().getType().toString().compareToIgnoreCase(r2.getType().getType().toString()))
                .toList();
    }
}
