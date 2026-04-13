package com.example.domain.admin.controller;

import com.example.domain.member.dto.MemberPageResponse;
import com.example.domain.member.repository.MemberRepository;
import com.example.domain.member.service.MemberService;
import com.example.global.rsData.RsData;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {
    private final MemberService memberService;
    @GetMapping("/members")
    public ResponseEntity<RsData<MemberPageResponse>> getMemberList(
            @PageableDefault(
                    size = 10,
                    sort = "createdAt",
                    direction = Sort.Direction.DESC
            ) Pageable pageable) {
        MemberPageResponse memberPage = memberService.getAllMembers(pageable);
        return ResponseEntity.ok(
                new RsData<>(
                        "200",
                        "회원 목록 조회 성공",
                        memberPage
                )
        );
    }
}
