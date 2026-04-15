package com.example.domain.member.controller;

import com.example.domain.member.dto.request.LoginRequest;
import com.example.domain.member.dto.request.SignupRequest;
import com.example.domain.member.dto.response.LoginResponse;
import com.example.domain.member.dto.response.SignupResponse;
import com.example.domain.member.service.AuthService;
import com.example.global.response.ApiRes;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@Tag(name = "Auth", description = "회원 인증 API")
public class AuthController {

    private final AuthService authService;

    // 회원가입 요청을 받아 서비스 계층으로 전달한다.
    // 성공 시 201 상태코드와 함께 회원가입 결과를 반환한다.
    @PostMapping("/signup")
    @Operation(summary = "회원가입", description = "이름, 아이디, 비밀번호, 이메일, 닉네임을 입력받아 회원가입을 처리합니다.")
    public ResponseEntity<ApiRes<SignupResponse>> signup(
            @Valid @RequestBody SignupRequest request
    ) {
        SignupResponse response = authService.signup(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiRes<>(201, "회원가입 성공", response));
    }

    // 로그인 요청을 받아 회원 검증 후 로그인 결과를 반환한다.
    @PostMapping("/login")
    @Operation(summary = "로그인", description = "loginId와 비밀번호를 검증한 뒤 로그인 결과를 반환합니다.")
    public ResponseEntity<ApiRes<LoginResponse>> login(
            @Valid @RequestBody LoginRequest request
    ) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(new ApiRes<>(200, "로그인 성공", response));
    }
}
