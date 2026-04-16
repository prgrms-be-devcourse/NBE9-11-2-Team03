package com.example.domain.reviewreport.repository;

import com.example.domain.reviewreport.entity.ReviewReport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewReportRepository extends JpaRepository<ReviewReport, Long> {

    // 같은 회원이 같은 리뷰를 이미 신고했는지 확인합니다.
    boolean existsByReporterIdAndReviewId(Long reporterId, Long reviewId);

    // 관리자가 특정 리뷰에 접수된 신고 사유 목록을 확인할 때 사용합니다.
    Page<ReviewReport> findByReviewId(Long reviewId, Pageable pageable);
}
