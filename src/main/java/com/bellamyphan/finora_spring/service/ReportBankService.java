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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportBankService {

    private final ReportRepository reportRepository;
    private final ReportBankRepository reportBankRepository;
    private final BankRepository bankRepository;

    /**
     * Returns the list of bank balances for a given report.
     * - If report is POSTED → read snapshot from DB
     * - If report is PENDING → calculate LIVE from transactions
     * NO DB writes happen here.
     */
    @Transactional(readOnly = true)
    public List<ReportBankBalanceDto> getBankBalances(Report report) {

        List<ReportBankBalanceDto> balances;

        if (report.isPosted()) {
            // Read snapshot from report_bank table (mutable list)
            balances = reportBankRepository.findByReportId(report.getId())
                    .stream()
                    .map(rb -> {
                        Bank bank = bankRepository.getReferenceById(rb.getBank().getId());
                        return new ReportBankBalanceDto(
                                bank.getId(),
                                rb.getBalance(),
                                bank.getName(),
                                bank.getGroup().getName()
                        );
                    })
                    .collect(Collectors.toCollection(ArrayList::new));
        } else {
            // Live calculation for pending reports
            balances = calculateLiveBankBalances(report);
        }

        // Sort safely
        sortBankBalances(balances);

        return balances;
    }

    /**
     * Calculate live bank balances from transactions.
     * Returns one entry per bank (multiple banks per report supported)
     * These entities are not saved — only for returning to controller/DTO.
     */
    private List<ReportBankBalanceDto> calculateLiveBankBalances(Report report) {

        List<ReportBankAggregate> aggregates =
                reportRepository.calculateLiveBankBalances(report.getId());

        // Map aggregates to DTO with bank and group info (mutable list)
        List<ReportBankBalanceDto> balances = aggregates.stream()
                .map(a -> {
                    Bank bank = bankRepository.getReferenceById(a.getBankId());
                    return new ReportBankBalanceDto(
                            bank.getId(),
                            a.getTotalAmount(),
                            bank.getName(),
                            bank.getGroup().getName()
                    );
                })
                .collect(Collectors.toCollection(ArrayList::new));

        // Sort safely
        sortBankBalances(balances);

        return balances;
    }

    /**
     * Sort bank balances by group name, then by bank name.
     */
    private void sortBankBalances(List<ReportBankBalanceDto> balances) {
        balances.sort(Comparator
                .comparing(ReportBankBalanceDto::bankGroupName, String.CASE_INSENSITIVE_ORDER)
                .thenComparing(ReportBankBalanceDto::bankName, String.CASE_INSENSITIVE_ORDER));
    }
}
