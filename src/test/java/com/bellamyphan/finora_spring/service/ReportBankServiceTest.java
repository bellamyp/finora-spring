package com.bellamyphan.finora_spring.service;

import com.bellamyphan.finora_spring.entity.Bank;
import com.bellamyphan.finora_spring.entity.Report;
import com.bellamyphan.finora_spring.entity.ReportBank;
import com.bellamyphan.finora_spring.repository.BankRepository;
import com.bellamyphan.finora_spring.repository.ReportBankAggregate;
import com.bellamyphan.finora_spring.repository.ReportBankRepository;
import com.bellamyphan.finora_spring.repository.ReportRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReportBankServiceTest {

    private ReportRepository reportRepository;
    private ReportBankRepository reportBankRepository;
    private BankRepository bankRepository;
    private ReportBankService reportBankService;

    @BeforeEach
    void setUp() {
        reportRepository = mock(ReportRepository.class);
        reportBankRepository = mock(ReportBankRepository.class);
        bankRepository = mock(BankRepository.class);
        reportBankService = new ReportBankService(reportRepository, reportBankRepository, bankRepository);
    }

    @Test
    void saveBankBalances_shouldThrowIfReportPosted() {
        Report postedReport = new Report();
        postedReport.setPosted(true);

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> reportBankService.saveBankBalances(postedReport));

        assertEquals("Cannot save bank balances for a posted report", ex.getMessage());
        verifyNoInteractions(reportRepository, reportBankRepository, bankRepository);
    }

    @Test
    void saveBankBalances_shouldCalculateAndPersistBalances() {
        Report pendingReport = new Report();
        pendingReport.setId("r1");
        pendingReport.setPosted(false);

        // Mock projection interface
        ReportBankAggregate agg1 = mock(ReportBankAggregate.class);
        when(agg1.getBankId()).thenReturn("b1");
        when(agg1.getTotalAmount()).thenReturn(BigDecimal.valueOf(100));

        when(reportRepository.calculateLiveBankBalances("r1")).thenReturn(List.of(agg1));

        Bank bank = new Bank();
        bank.setId("b1");
        when(bankRepository.getReferenceById("b1")).thenReturn(bank);

        reportBankService.saveBankBalances(pendingReport);

        ArgumentCaptor<List<ReportBank>> captor = ArgumentCaptor.forClass(List.class);
        verify(reportBankRepository).saveAll(captor.capture());

        List<ReportBank> saved = captor.getValue();
        assertEquals(1, saved.size());
        assertEquals(pendingReport, saved.get(0).getReport());
        assertEquals(bank, saved.get(0).getBank());
        assertEquals(BigDecimal.valueOf(100), saved.get(0).getBalance());
    }

    @Test
    void getBankBalances_shouldReturnSnapshotIfPosted() {
        Report postedReport = new Report();
        postedReport.setId("r2");
        postedReport.setPosted(true);

        ReportBank snapshot = new ReportBank();
        snapshot.setBalance(BigDecimal.valueOf(500));

        when(reportBankRepository.findByReportId("r2")).thenReturn(List.of(snapshot));

        List<ReportBank> result = reportBankService.getBankBalances(postedReport);
        assertEquals(1, result.size());
        assertEquals(snapshot, result.get(0));
        verify(reportBankRepository).findByReportId("r2");
        verifyNoInteractions(reportRepository, bankRepository);
    }

    @Test
    void getBankBalances_shouldCalculateLiveIfPending() {
        Report pendingReport = new Report();
        pendingReport.setId("r3");
        pendingReport.setPosted(false);

        // Mock projection interface
        ReportBankAggregate agg = mock(ReportBankAggregate.class);
        when(agg.getBankId()).thenReturn("b2");
        when(agg.getTotalAmount()).thenReturn(BigDecimal.valueOf(200));

        when(reportRepository.calculateLiveBankBalances("r3")).thenReturn(List.of(agg));

        Bank bank = new Bank();
        bank.setId("b2");
        when(bankRepository.getReferenceById("b2")).thenReturn(bank);

        List<ReportBank> result = reportBankService.getBankBalances(pendingReport);

        assertEquals(1, result.size());
        assertEquals(pendingReport, result.get(0).getReport());
        assertEquals(bank, result.get(0).getBank());
        assertEquals(BigDecimal.valueOf(200), result.get(0).getBalance());
    }
}