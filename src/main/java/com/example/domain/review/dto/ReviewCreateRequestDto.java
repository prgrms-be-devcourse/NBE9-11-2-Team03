package com.example.domain.review.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReviewCreateRequestDto {

    @NotBlank
    @Schema(description = "리뷰 내용", example = "아이랑 가기 정말 좋았어요.")
    private String content;

    @Schema(description = "리뷰 이미지 URL", example = "https://example.com/review-image.jpg")
    private String image;

    @Min(1)
    @Max(5)
    @Schema(description = "별점", example = "5")
    private Integer rating;



}
