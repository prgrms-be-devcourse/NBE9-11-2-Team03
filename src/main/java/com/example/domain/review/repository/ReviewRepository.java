package com.example.domain.review.repository;

import com.example.domain.review.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.example.domain.review.entity.ReviewStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReviewRepository extends JpaRepository<Review,Long> {

    Page<Review> findByFestivalId(Long festivalId, Pageable pageable);
    Page<Review> findAllByReportCountGreaterThanEqualAndStatus(int reportCount, ReviewStatus status, Pageable pageable);

    @Query("select avg(r.rating) from Review r where r.festival.id = :festivalId and r.status = com.example.domain.review.entity.ReviewStatus.ACTIVE")
    Double calculateAverageRatingByFestivalId(@Param("festivalId") Long festivalId);
}
