package com.bellamyphan.finora_spring.repository;

import com.bellamyphan.finora_spring.entity.ReportBank;
import com.bellamyphan.finora_spring.entity.ReportBankId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportBankRepository extends JpaRepository<ReportBank, ReportBankId> {
    List<ReportBank> findByReportId(String reportId);
}