package com.example.domain.member.dto.response;

import lombok.Getter;

@Getter
// refresh token 검증 후 새로 발급한 토큰을 내려주는 응답 DTO입니다.
public class TokenReissueResponse {

    private final String accessToken;
    private final String refreshToken;

    private TokenReissueResponse(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public static TokenReissueResponse of(String accessToken, String refreshToken) {
        return new TokenReissueResponse(accessToken, refreshToken);
    }
}
