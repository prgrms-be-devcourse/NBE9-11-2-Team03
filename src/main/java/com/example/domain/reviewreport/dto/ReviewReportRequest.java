package com.example.domain.reviewreport.dto;

import jakarta.validation.constraints.NotBlank;

public record ReviewReportRequest(
        @NotBlank(message = "신고 사유를 입력해야 합니다.")
        String reason
) {
}
