package com.example.domain.member.service;

import com.example.domain.member.dto.response.MyPageRes;
import com.example.domain.member.repository.MemberRepository;
import com.example.domain.review.repository.ReviewRepository;
import com.example.domain.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MyPageService {
    private final MemberRepository memberRepository;
    private final ReviewRepository reviewRepository;


    public MyPageRes getMyPage(String loginId) {

    }
}
