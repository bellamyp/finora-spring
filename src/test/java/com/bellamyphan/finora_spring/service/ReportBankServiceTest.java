package com.bellamyphan.finora_spring.service;

import com.bellamyphan.finora_spring.dto.ReportBankBalanceDto;
import com.bellamyphan.finora_spring.entity.Bank;
import com.bellamyphan.finora_spring.entity.BankGroup;
import com.bellamyphan.finora_spring.entity.Report;
import com.bellamyphan.finora_spring.repository.BankRepository;
import com.bellamyphan.finora_spring.repository.ReportBankAggregate;
import com.bellamyphan.finora_spring.repository.ReportBankRepository;
import com.bellamyphan.finora_spring.repository.ReportRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReportBankServiceTest {

    @Mock
    private ReportRepository reportRepository;

    @Mock
    private ReportBankRepository reportBankRepository;

    @Mock
    private BankRepository bankRepository;

    @InjectMocks
    private ReportBankService reportBankService;

    private Report postedReport;
    private Report pendingReport;
    private Bank bank1;
    private Bank bank2;
    private BankGroup group1;
    private BankGroup group2;

    @BeforeEach
    void setUp() {
        group1 = new BankGroup();
        group1.setId("group1");
        group1.setName("Group A");

        group2 = new BankGroup();
        group2.setId("group2");
        group2.setName("Group B");

        bank1 = new Bank();
        bank1.setId("bank1");
        bank1.setName("Bank One");
        bank1.setGroup(group1);

        bank2 = new Bank();
        bank2.setId("bank2");
        bank2.setName("Bank Two");
        bank2.setGroup(group2);

        postedReport = new Report();
        postedReport.setId("report1");
        postedReport.setPosted(true);

        pendingReport = new Report();
        pendingReport.setId("report2");
        pendingReport.setPosted(false);
    }

    @Test
    void getBankBalances_ShouldReturnPostedReportBalances() {
        // Mock DB snapshot for posted report
        var rb1 = new com.bellamyphan.finora_spring.entity.ReportBank();
        rb1.setBank(bank1);
        rb1.setBalance(BigDecimal.valueOf(100));

        var rb2 = new com.bellamyphan.finora_spring.entity.ReportBank();
        rb2.setBank(bank2);
        rb2.setBalance(BigDecimal.valueOf(200));

        when(reportBankRepository.findByReportId("report1")).thenReturn(List.of(rb2, rb1)); // unordered
        when(bankRepository.getReferenceById("bank1")).thenReturn(bank1);
        when(bankRepository.getReferenceById("bank2")).thenReturn(bank2);

        List<ReportBankBalanceDto> result = reportBankService.getBankBalances(postedReport);

        // Should sort by group name then bank name
        assertEquals(2, result.size());
        assertEquals("Group A", result.get(0).bankGroupName());
        assertEquals("Bank One", result.get(0).bankName());
        assertEquals(BigDecimal.valueOf(100), result.get(0).totalAmount());

        assertEquals("Group B", result.get(1).bankGroupName());
        assertEquals("Bank Two", result.get(1).bankName());
        assertEquals(BigDecimal.valueOf(200), result.get(1).totalAmount());

        verify(reportBankRepository, times(1)).findByReportId("report1");
        verify(bankRepository, times(2)).getReferenceById(anyString());
    }

    @Test
    void getBankBalances_ShouldReturnPendingReportBalances_WithProjectionInterface() {
        // Mock live calculation aggregates using interface implementation
        ReportBankAggregate agg1 = new ReportBankAggregate() {
            @Override
            public String getBankId() { return "bank1"; }
            @Override
            public BigDecimal getTotalAmount() { return BigDecimal.valueOf(300); }
        };

        ReportBankAggregate agg2 = new ReportBankAggregate() {
            @Override
            public String getBankId() { return "bank2"; }
            @Override
            public BigDecimal getTotalAmount() { return BigDecimal.valueOf(150); }
        };

        when(reportRepository.calculateLiveBankBalances("report2")).thenReturn(List.of(agg2, agg1));
        when(bankRepository.getReferenceById("bank1")).thenReturn(bank1);
        when(bankRepository.getReferenceById("bank2")).thenReturn(bank2);

        List<ReportBankBalanceDto> result = reportBankService.getBankBalances(pendingReport);

        // Sorted by group then bank name
        assertEquals(2, result.size());
        assertEquals("Group A", result.get(0).bankGroupName());
        assertEquals("Bank One", result.get(0).bankName());
        assertEquals(BigDecimal.valueOf(300), result.get(0).totalAmount());

        assertEquals("Group B", result.get(1).bankGroupName());
        assertEquals("Bank Two", result.get(1).bankName());
        assertEquals(BigDecimal.valueOf(150), result.get(1).totalAmount());

        verify(reportRepository, times(1)).calculateLiveBankBalances("report2");
        verify(bankRepository, times(2)).getReferenceById(anyString());
    }
}
