package com.bellamyphan.finora_spring.service;

import com.bellamyphan.finora_spring.constant.TransactionTypeEnum;
import com.bellamyphan.finora_spring.entity.Report;
import com.bellamyphan.finora_spring.entity.ReportType;
import com.bellamyphan.finora_spring.entity.TransactionType;
import com.bellamyphan.finora_spring.repository.ReportRepository;
import com.bellamyphan.finora_spring.repository.ReportTypeAggregate;
import com.bellamyphan.finora_spring.repository.ReportTypeRepository;
import com.bellamyphan.finora_spring.repository.TransactionTypeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class ReportTypeServiceTest {

    private ReportRepository reportRepository;
    private ReportTypeRepository reportTypeRepository;
    private TransactionTypeRepository transactionTypeRepository;

    private ReportTypeService service;

    @BeforeEach
    void setUp() {
        reportRepository = mock(ReportRepository.class);
        reportTypeRepository = mock(ReportTypeRepository.class);
        transactionTypeRepository = mock(TransactionTypeRepository.class);

        service = new ReportTypeService(reportRepository, reportTypeRepository, transactionTypeRepository);
    }

    @Test
    void testGetTypeBalances_PostedReport_ReturnsSnapshot() {
        Report report = mock(Report.class);
        when(report.isPosted()).thenReturn(true);
        when(report.getId()).thenReturn("r1");

        TransactionType typeA = mock(TransactionType.class);
        when(typeA.getType()).thenReturn(TransactionTypeEnum.INCOME);
        TransactionType typeB = mock(TransactionType.class);
        when(typeB.getType()).thenReturn(TransactionTypeEnum.SAVINGS);

        ReportType rt1 = new ReportType(report, typeB, BigDecimal.valueOf(200));
        ReportType rt2 = new ReportType(report, typeA, BigDecimal.valueOf(100));

        when(reportTypeRepository.findByReportId("r1")).thenReturn(List.of(rt1, rt2));

        List<ReportType> result = service.getTypeBalances(report);

        // Should be sorted by type name (A before B)
        assertEquals(List.of(rt2, rt1), result);

        verify(reportTypeRepository).findByReportId("r1");
        verifyNoInteractions(reportRepository, transactionTypeRepository);
    }

    @Test
    void testGetTypeBalances_PendingReport_CalculatesLive() {
        Report report = mock(Report.class);
        when(report.isPosted()).thenReturn(false);
        when(report.getId()).thenReturn("r2");

        // Mock aggregate results as interface
        ReportTypeAggregate agg1 = mock(ReportTypeAggregate.class);
        when(agg1.getTypeId()).thenReturn("typeB");
        when(agg1.getTotalAmount()).thenReturn(BigDecimal.valueOf(200));

        ReportTypeAggregate agg2 = mock(ReportTypeAggregate.class);
        when(agg2.getTypeId()).thenReturn("typeA");
        when(agg2.getTotalAmount()).thenReturn(BigDecimal.valueOf(100));

        when(reportRepository.calculateLiveTypeBalances("r2")).thenReturn(List.of(agg1, agg2));

        // Mock transaction types returned by repository
        TransactionType txA = mock(TransactionType.class);
        when(txA.getType()).thenReturn(TransactionTypeEnum.INCOME);

        TransactionType txB = mock(TransactionType.class);
        when(txB.getType()).thenReturn(TransactionTypeEnum.SAVINGS);

        when(transactionTypeRepository.getReferenceById("typeA")).thenReturn(txA);
        when(transactionTypeRepository.getReferenceById("typeB")).thenReturn(txB);

        List<ReportType> result = service.getTypeBalances(report);

        // Should be sorted by type name (A before B)
        assertEquals(2, result.size());
        assertEquals(TransactionTypeEnum.INCOME, result.get(0).getType().getType());
        assertEquals(TransactionTypeEnum.SAVINGS, result.get(1).getType().getType());

        verify(reportRepository).calculateLiveTypeBalances("r2");
        verify(transactionTypeRepository).getReferenceById("typeA");
        verify(transactionTypeRepository).getReferenceById("typeB");
        verifyNoInteractions(reportTypeRepository);
    }
}
