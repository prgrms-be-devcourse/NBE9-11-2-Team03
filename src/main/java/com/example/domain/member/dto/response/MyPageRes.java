package com.example.domain.member.dto.response;

import com.example.domain.member.entity.Member;

public record MyPageRes(
        Long memberId,
        String email,
        String nickname,
        long reviewCount,
        long bookMarkCount
) {
}
