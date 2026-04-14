package com.example.domain.member.dto.response;

import com.example.domain.member.entity.Member;
import com.example.domain.member.entity.MemberStatus;
import com.example.domain.member.entity.Role;
import lombok.Getter;

@Getter
// Login Response DTO
public class LoginResponse {

    private final String accessToken;
    private final Long memberId;
    private final String loginId;
    private final String nickname;
    private final Role role;
    private final MemberStatus status;

    private LoginResponse(
            String accessToken,
            Long memberId,
            String loginId,
            String nickname,
            Role role,
            MemberStatus status
    ) {
        this.accessToken = accessToken;
        this.memberId = memberId;
        this.loginId = loginId;
        this.nickname = nickname;
        this.role = role;
        this.status = status;
    }

    public static LoginResponse of(String accessToken, Member member) {
        return new LoginResponse(
                accessToken,
                member.getId(),
                member.getLoginId(),
                member.getNickname(),
                member.getRole(),
                member.getStatus()
        );
    }
}
