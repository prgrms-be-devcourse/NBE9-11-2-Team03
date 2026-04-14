package com.example.domain.review.service;

import com.example.domain.admin.dto.AdminReviewBlindRes;
import com.example.domain.festival.entity.Festival;
import com.example.domain.festival.repository.FestivalRepository;
import com.example.domain.member.entity.Member;
import com.example.domain.member.repository.MemberRepository;
import com.example.domain.admin.dto.AdminReviewReportPageRes;
import com.example.domain.review.dto.ReviewCreateRequestDto;
import com.example.domain.review.dto.ReviewListResponseDto;
import com.example.domain.review.dto.ReviewPageResponseDto;
import com.example.domain.review.dto.ReviewResponseDto;
import com.example.domain.review.entity.Review;
import com.example.domain.review.entity.ReviewStatus;
import com.example.domain.review.repository.ReviewRepository;
import com.example.global.exception.UnauthorizedException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final MemberRepository memberRepository;
    private final FestivalRepository festivalRepository;

    //리뷰 작성
    @Transactional
    public ReviewResponseDto createReview(Long festivalId, Long memberId, ReviewCreateRequestDto requestDto){
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("회원이 존재 하지 않습니다."));
        Festival festival = festivalRepository.findById(festivalId)
                .orElseThrow(()-> new EntityNotFoundException("축제가 존재하지 않습니다."));

        Review review = new Review(
                member,
                festival,
                requestDto.getContent(),
                requestDto.getImage(),
                requestDto.getRating()
        );

        Review savedReview = reviewRepository.save(review);

        return new ReviewResponseDto(savedReview);

    }
    // 신고횟수가 5이상인 review리스트를 DTO로 반환하여 주는 함수
    public AdminReviewReportPageRes getReportReview(Pageable pageable) {
        Page<Review> reviews = reviewRepository.findAllByReportCountGreaterThanEqualAndStatus(5,ReviewStatus.ACTIVE,pageable);
        return AdminReviewReportPageRes.from(reviews);
    }

    //리뷰 목록조회
    public ReviewPageResponseDto getReviewList(Long festivalId, Long memberId, int page, int size) {

        // 1. 로그인 체크
        if (memberId == null) {
            throw new UnauthorizedException("리뷰 조회는 로그인 후 이용 가능합니다.");
        }

        // 2. 축제 존재 체크
        festivalRepository.findById(festivalId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 축제입니다."));

        // 3. 리뷰 조회
        PageRequest pageRequest = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        Page<Review> reviewPage = reviewRepository.findByFestivalId(festivalId, pageRequest);

        return ReviewPageResponseDto.builder()
                .festivalId(festivalId)
                .content(reviewPage.getContent().stream()
                        .map(ReviewListResponseDto::from)
                        .toList())
                .page(reviewPage.getNumber())
                .size(reviewPage.getSize())
                .totalElements(reviewPage.getTotalElements())
                .totalPages(reviewPage.getTotalPages())
                .hasNext(reviewPage.hasNext())
                .build();
    }

    @Transactional
    public AdminReviewBlindRes processReviewAction(Long reviewId, String action) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(()->new EntityNotFoundException("해당 리뷰를 찾을 수 없습니다."));//추후 변경 예정
        if ("BLIND".equalsIgnoreCase(action)) {
            review.reviewBlind();
            Member author = review.getMember();
            if(author!=null){
                author.increaseReportCount();
            }
        }
        else if ("DISMISS".equalsIgnoreCase(action)) {
            // 신고 횟수를 0으로 초기화 (무혐의 처리)
            review.reportCountReset();
        }
        else {
            throw new IllegalArgumentException("잘못된 처리 요청입니다: " + action);
        }
        return new AdminReviewBlindRes(
                review.getId(),
                review.getStatus(),
                review.getReportCount()
        );
    }

}

