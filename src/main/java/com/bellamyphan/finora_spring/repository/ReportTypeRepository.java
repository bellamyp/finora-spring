package com.bellamyphan.finora_spring.repository;

import com.bellamyphan.finora_spring.entity.ReportType;
import com.bellamyphan.finora_spring.entity.ReportTypeId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportTypeRepository extends JpaRepository<ReportType, ReportTypeId> {
    List<ReportType> findByReportId(String reportId);
}