package com.example.domain.review.service;

import com.example.domain.festival.entity.Festival;
import com.example.domain.festival.repository.FestivalRepository;
import com.example.domain.member.entity.Member;
import com.example.domain.member.repository.MemberRepository;
import com.example.domain.review.dto.ReviewCreateRequestDto;
import com.example.domain.review.dto.ReviewResponseDto;
import com.example.domain.review.entity.Review;
import com.example.domain.review.repository.ReviewRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final MemberRepository memberRepository;
    private final FestivalRepository festivalRepository;

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
}

