package com.example.domain.reviewlike.repository;

import com.example.domain.reviewlike.entity.ReviewLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewLikeRepository extends JpaRepository<ReviewLike,Long> {
}
