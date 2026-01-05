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

        // 1️⃣ Find previous report
        Optional<Report> previousReportOpt =
                reportRepository.findPreviousReport(report.getUser().getId(), report.getMonth());

        // 2️⃣ Determine previous balances
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

        // 3️⃣ Calculate current live balances
        List<ReportBankAggregate> currentAggregates =
                reportRepository.calculateLiveBankBalances(report.getId());

        // 4️⃣ Merge previous balances + current aggregates
        return currentAggregates.stream()
                .map(a -> {
                    Bank bank = bankRepository.getReferenceById(a.getBankId());

                    ReportBank previous = previousBalances.stream()
                            .filter(rb -> rb.getBank().getId().equals(bank.getId()))
                            .findFirst()
                            .orElse(null);

                    ReportBank rb = new ReportBank();
                    rb.setReport(report);
                    rb.setBank(bank);
                    rb.setBalance(a.getTotalAmount()
                            .add(previous != null ? previous.getBalance() : BigDecimal.ZERO));
                    return rb;
                })
                .collect(Collectors.toList());
    }
}
