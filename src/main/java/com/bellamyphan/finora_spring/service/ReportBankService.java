package com.bellamyphan.finora_spring.service;

import com.bellamyphan.finora_spring.entity.Bank;
import com.bellamyphan.finora_spring.entity.Report;
import com.bellamyphan.finora_spring.entity.ReportBank;
import com.bellamyphan.finora_spring.repository.BankRepository;
import com.bellamyphan.finora_spring.repository.ReportBankAggregate;
import com.bellamyphan.finora_spring.repository.ReportBankRepository;
import com.bellamyphan.finora_spring.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportBankService {

    private final ReportRepository reportRepository;
    private final ReportBankRepository reportBankRepository;
    private final BankRepository bankRepository;

    /**
     * Calculate LIVE bank balances and persist snapshot.
     * This is called ONLY when posting a report.
     */
    @Transactional
    public void saveBankBalances(Report report) {

        if (report.isPosted()) {
            throw new IllegalStateException("Cannot save bank balances for a posted report");
        }

        // 1️⃣ Calculate live balances
        List<ReportBank> balances = calculateLiveBankBalances(report);

        // 2️⃣ Persist snapshot
        reportBankRepository.saveAll(balances);
    }

    /**
     * Returns the list of bank balances for a given report.
     * - If report is POSTED → read snapshot from DB
     * - If report is PENDING → calculate LIVE from transactions
     * NO DB writes happen here.
     */
    @Transactional(readOnly = true)
    public List<ReportBank> getBankBalances(Report report) {
        if (report.isPosted()) {
            // Return snapshot entities
            return reportBankRepository.findByReportId(report.getId());
        } else {
            // Return live calculated entities (not saved)
            return calculateLiveBankBalances(report);
        }
    }

    /**
     * Calculate live bank balances from transactions.
     * Returns one entry per bank (multiple banks per report supported)
     * These entities are not saved — only for returning to controller/DTO.
     */
    private List<ReportBank> calculateLiveBankBalances(Report report) {

        LocalDate startOfMonth = report.getMonth();
        LocalDate endOfMonth = report.getMonth().withDayOfMonth(report.getMonth().lengthOfMonth());

        // 1️⃣ Get all banks active in this month
        List<Bank> activeBanks = bankRepository.findActiveBanksInMonth(
                report.getUser().getId(),
                startOfMonth,
                endOfMonth
        );


        // 2️⃣ Get previous report (if any)
        Optional<Report> previousReportOpt =
                reportRepository.findFirstByUserIdAndMonthBeforeOrderByMonthDesc(report.getUser().getId(), report.getMonth());

        // Determine previous balances
        List<ReportBank> previousBalances;
        if (previousReportOpt.isEmpty()) {
            // First-ever report → previous balances = 0
            previousBalances = List.of();
        } else {
            Report previousReport = previousReportOpt.get();
            if (!previousReport.isPosted()) {
                throw new IllegalStateException(
                        "Cannot calculate bank balances because the previous report is not posted: "
                                + previousReport.getId());
            }
            // Previous report is posted → read snapshot
            previousBalances = reportBankRepository.findByReportId(previousReport.getId());
        }

        // 3️⃣ Get current live aggregates (transactions)
        List<ReportBankAggregate> currentAggregates =
                reportRepository.calculateLiveBankBalances(report.getId());

        // 4️⃣ Build a map for cumulative balances
        var previousMap = previousBalances.stream()
                .collect(Collectors.toMap(rb -> rb.getBank().getId(), ReportBank::getBalance));

        var currentMap = currentAggregates.stream()
                .collect(Collectors.toMap(ReportBankAggregate::getBankId, ReportBankAggregate::getTotalAmount));

        // 5️⃣ Merge everything, ensuring all active banks included
        return activeBanks.stream()
                .map(bank -> {
                    BigDecimal prev = previousMap.getOrDefault(bank.getId(), BigDecimal.ZERO);
                    BigDecimal curr = currentMap.getOrDefault(bank.getId(), BigDecimal.ZERO);
                    ReportBank rb = new ReportBank();
                    rb.setReport(report);
                    rb.setBank(bank);
                    rb.setBalance(prev.add(curr));
                    return rb;
                })
                .collect(Collectors.toList());
    }
}
