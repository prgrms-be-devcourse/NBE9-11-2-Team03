package com.example.domain.member.controller;

import com.example.domain.member.dto.response.MyPageRes;
import com.example.domain.member.dto.response.MyPageReviewRes;
import com.example.domain.member.service.MemberService;
import com.example.domain.member.service.MyPageService;
import com.example.global.rsData.RsData;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users/me")
@RequiredArgsConstructor
public class MyPageController {
    private final MyPageService myPageService;

    /**
     * 마이페이지 조회를 하여 사용자가 자신의 정보를 얻을 수 있다.
     * @param authentication 로그인 토큰 로그인 토큰을 통해 loginId를 얻는다.
     * @return 로그인id,이메일,닉네임,리뷰 수 ,찜수를 담은 DTo를 RSDATA로 감싸여 반환
     */
    @GetMapping()
    public ResponseEntity<RsData<MyPageRes>> getMyPage(
            Authentication authentication
    ){
        String loginId = authentication.getName();
        MyPageRes res = myPageService.getMyPage(loginId);
        return ResponseEntity.ok(
                new RsData<>(
                        "200",
                        "마이페이지 조회에 성공하였습니다.",
                        res
                )
        );
    }

 /*   @GetMapping("/reviews")
    public ResponseEntity<RsData<MyPageReviewRes>> getMyReview(){

    }

  */
}
