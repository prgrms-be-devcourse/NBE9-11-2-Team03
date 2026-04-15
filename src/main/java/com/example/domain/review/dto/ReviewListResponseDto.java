package com.example.domain.review.dto;


import com.example.domain.review.entity.Review;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ReviewListResponseDto {

    private Long reviewId;
    private Long memberId;
    private Long festivalId;
    private String nickname;
    private String content;
    private Integer rating;
    private String image;
    private Integer likeCount;
    private Integer reportCount;
    private LocalDateTime createdAt;

    public static ReviewListResponseDto from(Review review) {
        return ReviewListResponseDto.builder()
                .reviewId(review.getId())
                .memberId(review.getMember().getId())
                .festivalId(review.getFestival().getId())
                .nickname(review.getMember().getNickname())
                .content(review.getContent())
                .image(review.getImage())
                .rating(review.getRating())
                .likeCount(review.getLikeCount())
                .reportCount(review.getReportCount())
                .createdAt(review.getCreatedAt())
                .build();


    }
}
