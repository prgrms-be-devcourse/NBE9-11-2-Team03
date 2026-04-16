package com.example.domain.admin.dto;

import com.example.domain.reviewreport.entity.ReviewReport;
import org.springframework.data.domain.Page;

import java.util.List;

public record AdminReviewReportDetailPageRes(
        List<AdminReviewReportDetailRes> content,
        int page,
        int size,
        long totalElements,
        int totalPages
) {
    public static AdminReviewReportDetailPageRes from(Page<ReviewReport> reportPage) {
        return new AdminReviewReportDetailPageRes(
                reportPage.getContent().stream()
                        .map(AdminReviewReportDetailRes::from)
                        .toList(),
                reportPage.getNumber(),
                reportPage.getSize(),
                reportPage.getTotalElements(),
                reportPage.getTotalPages()
        );
    }
}
