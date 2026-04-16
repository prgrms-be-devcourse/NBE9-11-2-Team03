package com.example.domain.reviewreport.controller;

import com.example.domain.reviewreport.dto.ReviewReportResponse;
import com.example.domain.reviewreport.service.ReviewReportService;
import com.example.global.response.ApiRes;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ReviewReportController {

    private final ReviewReportService reviewReportService;

    @PostMapping("/reviews/{reviewId}/reports")
    public ResponseEntity<ApiRes<ReviewReportResponse>> reportReview(
            @PathVariable Long reviewId,
            Authentication authentication
    ) {
        // Security에서 인증된 사용자의 loginId를 꺼내 신고 서비스로 전달합니다.
        String loginId = authentication.getName();
        ReviewReportResponse response = reviewReportService.reportReview(reviewId, loginId);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiRes<>(201, "리뷰 신고가 성공적으로 접수되었습니다.", response));
    }
}
