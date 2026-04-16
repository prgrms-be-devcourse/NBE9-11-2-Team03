package com.example.domain.member.controller;

import com.example.domain.member.dto.response.MyPageRes;
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
}
