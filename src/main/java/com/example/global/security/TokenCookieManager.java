package com.example.global.security;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.Duration;

@Component
public class TokenCookieManager {

    public static final String REFRESH_TOKEN_COOKIE_NAME = "refreshToken";

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String COOKIE_PATH = "/";
    private static final String SAME_SITE = "Lax";

    @Value("${jwt.refresh-token-expiration-ms:1209600000}")
    private long refreshTokenExpirationMs;

    public void addRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
        // refresh token만 HttpOnly 쿠키로 내려줌.
        addCookie(response, REFRESH_TOKEN_COOKIE_NAME, refreshToken, Duration.ofMillis(refreshTokenExpirationMs));
    }

    public void clearRefreshTokenCookie(HttpServletResponse response) {
        // 로그아웃 시 브라우저에 남은 refresh token 쿠키를 삭제함.
        addCookie(response, REFRESH_TOKEN_COOKIE_NAME, "", Duration.ZERO);
    }

    public String resolveAccessToken(HttpServletRequest request) {
        // access token은 기존 방식대로 Authorization 헤더에서만 꺼냄.
        return extractBearerToken(request);
    }

    public String resolveRefreshToken(HttpServletRequest request) {
        return extractCookieValue(request, REFRESH_TOKEN_COOKIE_NAME);
    }

    private void addCookie(HttpServletResponse response, String name, String value, Duration maxAge) {
        ResponseCookie cookie = ResponseCookie.from(name, value)
                .httpOnly(true)
                .secure(false)
                .sameSite(SAME_SITE)
                .path(COOKIE_PATH)
                .maxAge(maxAge)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    private String extractBearerToken(HttpServletRequest request) {
        String authorizationHeader = request.getHeader(AUTHORIZATION_HEADER);

        if (!StringUtils.hasText(authorizationHeader) || !authorizationHeader.startsWith(BEARER_PREFIX)) {
            return null;
        }

        return authorizationHeader.substring(BEARER_PREFIX.length());
    }

    private String extractCookieValue(HttpServletRequest request, String cookieName) {
        Cookie[] cookies = request.getCookies();

        if (cookies == null) {
            return null;
        }

        for (Cookie cookie : cookies) {
            if (cookieName.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }

        return null;
    }
}
