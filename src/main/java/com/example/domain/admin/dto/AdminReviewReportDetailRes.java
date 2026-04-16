package com.example.domain.admin.dto;

import com.example.domain.reviewreport.entity.ReviewReport;

import java.time.LocalDateTime;

public record AdminReviewReportDetailRes(
        Long reportId,
        Long reporterId,
        String reporterNickname,
        String reason,
        LocalDateTime createdAt
) {
    public static AdminReviewReportDetailRes from(ReviewReport reviewReport) {
        return new AdminReviewReportDetailRes(
                reviewReport.getId(),
                reviewReport.getReporter().getId(),
                reviewReport.getReporter().getNickname(),
                reviewReport.getReason(),
                reviewReport.getCreatedAt()
        );
    }
}
