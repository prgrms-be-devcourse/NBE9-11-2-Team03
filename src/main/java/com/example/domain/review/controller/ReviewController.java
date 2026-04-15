package com.example.domain.review.controller;

import com.example.domain.review.dto.*;
import com.example.domain.review.service.ReviewService;
import com.example.global.response.ApiRes;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
    @Operation(summary = "축제 리뷰 작성", description = "특정 축제에 리뷰를 작성합니다.")
    public ResponseEntity<ApiRes<ReviewResponseDto>> createReview(
            @PathVariable Long festivalId,
            @Valid @RequestBody ReviewCreateRequestDto requestDto
            ){
        ReviewResponseDto response = reviewService.createReview(festivalId, TEST_MEMBER_ID, requestDto);

        return ResponseEntity.status(HttpStatus.CREATED)
                        .body(new ApiRes<>(201, "리뷰 작성이 완료 되었습니다.", response));
    }


    @GetMapping("/festivals/{festivalId}/reviews")
    @Operation(summary = "축제 리뷰 목록 조회", description = "특정 축제의 리뷰 목록을 페이징하여 조회합니다.")
    public ResponseEntity<ApiRes<ReviewPageResponseDto>> getReviewList(
            @PathVariable Long festivalId,
            @RequestParam(required = false) Long memberId,
            @Parameter(description = "페이지 번호", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기", example = "10")
            @RequestParam(defaultValue = "10") int size
    ) {
        ReviewPageResponseDto response = reviewService.getReviewList(festivalId, memberId, page, size);

        return ResponseEntity.ok(
                new ApiRes<>(200, "축제 리뷰 목록 조회 성공", response)
        );
    }


    @PatchMapping("/reviews/{reviewId}")
    @Operation(summary = "축제 리뷰 수정", description = "본인이 작성한 리뷰를 수정합니다.")
    public ResponseEntity<ApiRes<ReviewUpdateResponseDto>> updateReview(
            @PathVariable Long reviewId,
            @Valid @RequestBody ReviewUpdateRequestDto requestDto
    ) {
        ReviewUpdateResponseDto response = reviewService.updateReview(reviewId, TEST_MEMBER_ID, requestDto);

        return ResponseEntity.ok(
                new ApiRes<>(200, "리뷰가 성공적으로 수정되었습니다.", response)
        );
    }


    @DeleteMapping("/reviews/{reviewId}")
    @Operation(summary = "축제 리뷰 삭제", description = "본인이 작성한 리뷰를 삭제합니다.")
    public ResponseEntity<ApiRes<ReviewDeleteResponseDto>> deleteReview(
            @PathVariable Long reviewId
    ) {
        ReviewDeleteResponseDto response = reviewService.deleteReview(reviewId, TEST_MEMBER_ID);

        return ResponseEntity.ok(
                new ApiRes<>(200, "리뷰 삭제가 완료되었습니다.", response)
        );
    }


}
