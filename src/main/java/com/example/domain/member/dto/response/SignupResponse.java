package com.example.domain.member.dto.response;

import com.example.domain.member.entity.Member;
import com.example.domain.member.entity.MemberStatus;
import com.example.domain.member.entity.Role;
import lombok.Getter;

@Getter
// Signup Response DTO
public class SignupResponse {

    private final Long memberId;
    private final String loginId;
    private final String nickname;
    private final Role role;
    private final MemberStatus status;

    private SignupResponse(
            Long memberId,
            String loginId,
            String nickname,
            Role role,
            MemberStatus status
    ) {
        this.memberId = memberId;
        this.loginId = loginId;
        this.nickname = nickname;
        this.role = role;
        this.status = status;
    }

    public static SignupResponse from(Member member) {
        return new SignupResponse(
                member.getId(),
                member.getLoginId(),
                member.getNickname(),
                member.getRole(),
                member.getStatus()
        );
    }
}
