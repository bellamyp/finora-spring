package com.bellamyphan.finora_spring.service;

import com.bellamyphan.finora_spring.dto.ReportBankBalanceDto;
import com.bellamyphan.finora_spring.entity.Bank;
import com.bellamyphan.finora_spring.entity.Report;
import com.bellamyphan.finora_spring.repository.BankRepository;
import com.bellamyphan.finora_spring.repository.ReportBankAggregate;
import com.bellamyphan.finora_spring.repository.ReportBankRepository;
import com.bellamyphan.finora_spring.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportBankService {

    private final ReportRepository reportRepository;
    private final ReportBankRepository reportBankRepository;
    private final BankRepository bankRepository;

    /**
     * Returns the list of bank balances for a given report.
     * - If report is POSTED → read snapshot from DB (TODO later)
     * - If report is PENDING → calculate LIVE from transactions
     * NO DB writes happen here.
     */
    @Transactional(readOnly = true)
    public List<ReportBankBalanceDto> getBankBalances(Report report) {

        if (report.isPosted()) {
            // Read snapshot from report_bank table
            return reportBankRepository.findByReportId(report.getId())
                    .stream()
                    .map(rb -> {
                        Bank bank = bankRepository.getReferenceById(rb.getBank().getId());
                        return new ReportBankBalanceDto(bank.getId(), rb.getBalance());
                    })
                    .toList();
        }

        // Live calculation for pending reports
        return calculateLiveBankBalances(report);
    }

    /**
     * Calculate live bank balances from transactions.
     * Returns one entry per bank (multiple banks per report supported)
     * These entities are not saved — only for returning to controller/DTO.
     */
    private List<ReportBankBalanceDto> calculateLiveBankBalances(Report report) {

        // TODO: implement snapshot reading for posted previous report!

        List<ReportBankAggregate> aggregates =
                reportRepository.calculateLiveBankBalances(report.getId());

        return aggregates.stream()
                .map(a -> {
                    Bank bank = bankRepository.getReferenceById(a.getBankId());
                    return new ReportBankBalanceDto(
                            bank.getId(),
                            a.getTotalAmount()
                    );
                })
                .toList();
    }
}
