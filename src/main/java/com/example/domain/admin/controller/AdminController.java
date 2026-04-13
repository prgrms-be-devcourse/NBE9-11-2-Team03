package com.example.domain.admin.controller;

import com.example.domain.member.dto.MemberPageResponse;
import com.example.domain.member.repository.MemberRepository;
import com.example.domain.member.service.MemberService;
import com.example.global.rsData.RsData;
import lombok.Data;
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



    /**
     *전체 회원 목록을 조회(기본 최근 생성일순,)
     *파라미터로 페이지번호,정렬방법,사이즈 변경 가능
     * @param pageable 페이지수 및  정렬정보(page,sort)
     * @return 회원 목록 정보를 담은 RsData객체
     */
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

    /**
     * 전체 회원목록중 신고횟수가 5번 이상이며 활동중인 회원 조회(기본:신고 누적 내림차순)
     * 파라미터로 페이지,사이즈 조절 가능
     * @param pageable 페이지수
     * @return 신고 누적횟수가5회이상인 활동중인 회원목록을 RsData객체
     */
    @GetMapping("/members/reported")
    public ResponseEntity<RsData<MemberPageResponse>> getReportMemberList(
            @PageableDefault(
                    size = 10,
                    sort = "reportCount",
                    direction = Sort.Direction.DESC
            ) Pageable pageable
    ){
        MemberPageResponse memberList = memberService.getReportMembers(pageable);
        return ResponseEntity.ok(
                new RsData<>(
                        "200",
                        "신고 누적회원 조회 성공",
                        memberList
                )
        );
    }
}
