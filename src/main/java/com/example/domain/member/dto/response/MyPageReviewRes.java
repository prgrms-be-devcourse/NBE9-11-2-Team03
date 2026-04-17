package com.example.domain.member.dto.response;

import com.example.domain.review.entity.Review;
import org.springframework.data.domain.Page;

import java.util.List;

public record MyPageReviewRes(
        List<MyReviewItemRes> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean hasNext
) {
    public static MyPageReviewRes from (Page<Review> reviewPageewPage){
        return new MyPageReviewRes(
                reviewPageewPage.getContent().stream()
                        .map(MyReviewItemRes::from)
                        .toList(),
                reviewPageewPage.getNumber(),
                reviewPageewPage.getSize(),
                reviewPageewPage.getTotalElements(),
                reviewPageewPage.getTotalPages(),
                reviewPageewPage.hasNext()
        );
    }
}
