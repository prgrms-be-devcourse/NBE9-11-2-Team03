package com.example.domain.review.service;

import com.example.domain.admin.dto.AdminReviewBlindRes;
import com.example.domain.admin.dto.AdminReviewReportPageRes;
import com.example.domain.festival.entity.Festival;
import com.example.domain.festival.repository.FestivalRepository;
import com.example.domain.member.dto.response.MyReviewPageRes;
import com.example.domain.member.entity.Member;
import com.example.domain.member.repository.MemberRepository;
import com.example.domain.review.dto.*;
import com.example.domain.review.entity.Review;
import com.example.domain.review.entity.ReviewStatus;
import com.example.domain.review.repository.ReviewRepository;
import com.example.global.exception.BadRequestException;
import com.example.global.exception.CustomNotFoundException;
import com.example.global.exception.ForbiddenException;
import com.example.global.exception.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final MemberRepository memberRepository;
    private final FestivalRepository festivalRepository;

    //리뷰 작성
    @Transactional
    public ReviewResponseDto createReview(Long festivalId, String loginId, ReviewCreateRequestDto requestDto){



        // 1. 로그인한 회원 조회
        Member member = memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> new UnauthorizedException("로그인한 회원 정보를 찾을 수 없습니다."));

        // 2. 축제 존재 여부 확인
        Festival festival = festivalRepository.findById(festivalId)
                .orElseThrow(() -> new CustomNotFoundException("축제가 존재하지 않습니다."));


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


    //리뷰 목록조회
    public ReviewPageResponseDto getReviewList(Long festivalId, String loginId, int page, int size) {

        // 1. 로그인 체크
        if (loginId == null || loginId.equals("anonymousUser")) {
            throw new UnauthorizedException("리뷰 조회는 로그인 후 이용 가능합니다.");
        }


        // 2. 축제 존재 체크
        festivalRepository.findById(festivalId)
                .orElseThrow(() -> new CustomNotFoundException("존재하지 않는 축제입니다."));

        // 3. 리뷰 조회
        PageRequest pageRequest = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        Page<Review> reviewPage = reviewRepository.findByFestivalIdAndStatus(
                festivalId,
                ReviewStatus.ACTIVE,
                pageRequest);

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



    //리뷰 수정
    @Transactional
    public ReviewUpdateResponseDto updateReview(Long reviewId, String loginId, ReviewUpdateRequestDto requestDto) {



        // 2. 로그인한 회원 조회
        Member member = memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> new UnauthorizedException("로그인한 회원 정보를 찾을 수 없습니다."));

        // 3. 리뷰 존재 여부 확인
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new CustomNotFoundException("존재하지 않는 리뷰입니다."));

        // 3. 작성자 본인 여부 확인
        if (!review.getMember().getId().equals(member.getId())) {
            throw new ForbiddenException("본인이 작성한 리뷰만 수정할 수 있습니다.");
        }

        // 4. 삭제된 리뷰 수정 불가
        if (review.getStatus() == ReviewStatus.DELETED) {
            throw new BadRequestException("삭제된 리뷰는 수정할 수 없습니다.");
        }

        // 5. 블라인드 리뷰 수정 불가
        if (review.getStatus() == ReviewStatus.BLIND) {
            throw new ForbiddenException("블라인드 처리된 리뷰는 수정할 수 없습니다.");
        }

        // 6. 평점 검증
        if (requestDto.getRating() < 1 || requestDto.getRating() > 5) {
            throw new BadRequestException("평점은 1점부터 5점까지 입력 가능합니다.");
        }

        // 7. 리뷰 수정
        review.updateReview(
                requestDto.getContent(),
                requestDto.getImage(),
                requestDto.getRating()
        );

        // 8. 평균 평점 재계산
        Festival festival = review.getFestival();
        Double averageRating = reviewRepository.calculateAverageRatingByFestivalId(festival.getId());
        festival.updateAverageRating(averageRating == null ? 0.0 : averageRating);

        return ReviewUpdateResponseDto.from(review);
    }

    //리뷰 삭제
    @Transactional
    public ReviewDeleteResponseDto deleteReview(Long reviewId, String loginId) {



        // 2. 로그인한 회원 조회
        Member member = memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> new UnauthorizedException("로그인한 회원 정보를 찾을 수 없습니다."));

        // 3. 리뷰 존재 여부 확인
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new CustomNotFoundException("존재하지 않는 리뷰입니다."));

        // 4. 작성자 본인 여부 확인
        if (!review.getMember().getId().equals(member.getId())) {
            throw new ForbiddenException("본인이 작성한 리뷰만 삭제할 수 있습니다.");
        }

        // 5. 이미 삭제된 리뷰인지 확인
        if (review.getStatus() == ReviewStatus.DELETED) {
            throw new BadRequestException("이미 삭제된 리뷰입니다.");
        }

        // 6. 블라인드 리뷰 삭제 불가
        if (review.getStatus() == ReviewStatus.BLIND) {
            throw new ForbiddenException("블라인드 처리된 리뷰는 삭제할 수 없습니다.");
        }

        // 7. 리뷰 논리 삭제
        review.deleteReview();

        // 8. 축제 평균 평점 재계산
        Festival festival = review.getFestival();
        Double averageRating = reviewRepository.calculateAverageRatingByFestivalId(festival.getId());
        festival.updateAverageRating(averageRating == null ? 0.0 : averageRating);

        return ReviewDeleteResponseDto.from(review);
    }




    // 신고횟수가 5이상인 review리스트를 DTO로 반환하여 주는 함수
    public AdminReviewReportPageRes getReportReview(Pageable pageable) {
        Page<Review> reviews = reviewRepository.findAllByReportCountGreaterThanEqualAndStatus(5,ReviewStatus.ACTIVE,pageable);
        return AdminReviewReportPageRes.from(reviews);
    }

    //리뷰를 검토하여 블라인드처리, 신고횟수 초기화하는 함수
    @Transactional
    public AdminReviewBlindRes processReviewAction(Long reviewId, String action) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(()->new CustomNotFoundException("404","존재하지 않는 리뷰입니다."));//추후 변경 예정
        if(review.getStatus()==ReviewStatus.DELETED){
            throw new BadRequestException("삭제된 리뷰는 상태를 변경할 수 없습니다.");
        }

        if ("BLIND".equalsIgnoreCase(action)) {
            if (review.getStatus() == ReviewStatus.BLIND) {
                throw new BadRequestException("이미 블라인드 처리된 리뷰입니다.");
            }
            review.reviewBlind();
            Member author = review.getMember();
            if(author != null){
                memberRepository.incrementReportCount(author.getId());
            }
        }
        else if ("DISMISS".equalsIgnoreCase(action)) {
            // 신고 횟수를 0으로 초기화 (무혐의 처리)
            review.reportCountReset();
        }
        else {
            throw new IllegalArgumentException("허용되지 않은 리뷰 상태입니다.: " + action);
        }
        return new AdminReviewBlindRes(
                review.getId(),
                review.getStatus(),
                review.getReportCount()
        );
    }
}

