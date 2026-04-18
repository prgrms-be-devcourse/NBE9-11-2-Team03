package com.example.domain.review.repository;

import com.example.domain.review.entity.Review;
import com.example.domain.review.entity.ReviewStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReviewRepository extends JpaRepository<Review,Long> {

    //특정 축제 리뷰 목록 조회(ACTIVE만 조회할 때 사용)
    Page<Review> findByFestivalIdAndStatus(Long festivalId, ReviewStatus status, Pageable pageable);

    //평균 평점
    @Query("select avg(r.rating) from Review r where r.festival.id = :festivalId and r.status = com.example.domain.review.entity.ReviewStatus.ACTIVE")
    Double calculateAverageRatingByFestivalId(@Param("festivalId") Long festivalId);

    Page<Review> findByFestivalId(Long festivalId, Pageable pageable);
    Page<Review> findAllByReportCountGreaterThanEqualAndStatus(int reportCount, ReviewStatus status, Pageable pageable);

    long countByMemberId(Long memberId);

    //사용자가 단 리뷰
    Page<Review> findByMemberIdAndStatus(Long memberId,ReviewStatus status,Pageable pageable);

}
