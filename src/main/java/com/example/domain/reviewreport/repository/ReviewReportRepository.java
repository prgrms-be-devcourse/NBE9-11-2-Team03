package com.example.domain.reviewreport.repository;

import com.example.domain.reviewreport.entity.ReviewReport;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewReportRepository extends JpaRepository<ReviewReport, Long> {
}
