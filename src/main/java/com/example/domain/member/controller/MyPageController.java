package com.example.domain.member.controller;

import com.example.domain.member.dto.response.MyPageRes;
import com.example.domain.member.service.MemberService;
import com.example.domain.member.service.MyPageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("/api/users/me")
@RequiredArgsConstructor
public class MyPageController {
    private final MyPageService myPageService;
    @GetMapping()
    public ResponseEntity<MyPageRes> getMyPage(
            Authentication authentication
    ){
        String loginId = authentication.getName();
        MyPageRes res = myPageService.getMyPage(loginId);
    }
}
