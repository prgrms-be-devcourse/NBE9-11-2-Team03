package com.example.domain.review.service;

import com.example.domain.festival.entity.Festival;
import com.example.domain.festival.repository.FestivalRepository;
import com.example.domain.member.entity.Member;
import com.example.domain.member.repository.MemberRepository;
import com.example.domain.review.dto.ReviewCreateRequestDto;
import com.example.domain.review.dto.ReviewListResponseDto;
import com.example.domain.review.dto.ReviewPageResponseDto;
import com.example.domain.review.dto.ReviewResponseDto;
import com.example.domain.review.entity.Review;
import com.example.domain.review.repository.ReviewRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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

    //리뷰 목록조회
    public ReviewPageResponseDto getReviewList(Long festivalId, int page, int size) {

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
}

