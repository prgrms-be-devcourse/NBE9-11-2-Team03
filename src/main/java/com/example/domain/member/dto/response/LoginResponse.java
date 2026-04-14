package com.example.domain.member.dto.response;

import com.example.domain.member.entity.Member;
import com.example.domain.member.entity.MemberStatus;
import com.example.domain.member.entity.Role;
import lombok.Getter;

@Getter
// 로그인 성공 시 access token과 회원 기본 정보를 함께 내려주는 DTO다.
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

    // 발급된 access token과 Member 엔티티를 조합해 로그인 응답 DTO를 만든다.
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
