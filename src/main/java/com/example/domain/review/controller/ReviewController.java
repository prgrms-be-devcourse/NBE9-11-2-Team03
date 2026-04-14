package com.example.domain.review.controller;

import com.example.domain.review.dto.ReviewCreateRequestDto;
import com.example.domain.review.dto.ReviewResponseDto;
import com.example.domain.review.service.ReviewService;
import com.example.global.response.ApiRes;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = "Review", description = "리뷰API")
public class ReviewController {

    private final ReviewService reviewService;

    //인증 연결 전 임시 테스트용
    private static final Long TEST_MEMBER_ID = 1L;

    @PostMapping("/festivals/{festivalId}/reviews")
    @Operation(summary = "리뷰 작성", description = "특정 축제에 리뷰를 작성합니다.")
    public ResponseEntity<ApiRes<ReviewResponseDto>> createReview(
            @PathVariable Long festivalId,
            @Valid @RequestBody ReviewCreateRequestDto requestDto
            ){
        ReviewResponseDto response = reviewService.createReview(festivalId, TEST_MEMBER_ID, requestDto);

        return ResponseEntity.status(HttpStatus.CREATED)
                        .body(new ApiRes<>(201, "리뷰 작성이 완료 되었습니다.", response));
    }
}
