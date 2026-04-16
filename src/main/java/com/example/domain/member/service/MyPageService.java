package com.example.domain.member.service;

import com.example.domain.bookmark.repository.FestivalBookmarkRepository;
import com.example.domain.festival.entity.Festival;
import com.example.domain.festival.repository.FestivalRepository;
import com.example.domain.member.dto.response.MyPageRes;
import com.example.domain.member.entity.Member;
import com.example.domain.member.entity.MemberStatus;
import com.example.domain.member.repository.MemberRepository;
import com.example.domain.review.repository.ReviewRepository;
import com.example.domain.review.service.ReviewService;
import com.example.global.exception.CustomNotFoundException;
import com.example.global.exception.ForbiddenException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MyPageService {
    private final MemberRepository memberRepository;
    private final ReviewRepository reviewRepository;
    private final FestivalBookmarkRepository festivalBookmarkRepository;


    public MyPageRes getMyPage(String loginId) {
        Member member = memberRepository.findByLoginId(loginId).orElseThrow(()->new CustomNotFoundException("로그인한 회원 정보를 찾을 수 없습니다."));
        if(member.getStatus()== MemberStatus.WITHDRAWN){
            throw new ForbiddenException("탈퇴한 회원은 마이페이지를 조회할 수 없습니다.");
        }
        long reviewCount = reviewRepository.countByMemberId(member.getId());
        long bookMarkCount= festivalBookmarkRepository.countByMemberId(member.getId());
        return new MyPageRes(
                member.getId(),
                member.getEmail(),
                member.getNickname(),
                reviewCount,
                bookMarkCount
        );
    }
}
