package com.example.domain.member.service;

import com.example.domain.member.dto.request.LoginRequest;
import com.example.domain.member.dto.request.SignupRequest;
import com.example.domain.member.dto.response.LoginResponse;
import com.example.domain.member.dto.response.SignupResponse;
import com.example.domain.member.entity.Member;
import com.example.domain.member.entity.MemberStatus;
import com.example.domain.member.repository.MemberRepository;
import com.example.global.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
// 회원가입과 로그인의 비즈니스 흐름을 담당하는 서비스다.
// 컨트롤러는 요청을 받고, 실제 처리 순서는 이 서비스가 조합한다.
public class AuthService {

    private final MemberRepository memberRepository;
    // 회원가입 때는 비밀번호를 암호화하고, 로그인 때는 입력값과 저장값을 비교한다.
    private final PasswordEncoder passwordEncoder;
    // 로그인 성공 후 access token을 만들기 위해 사용하는 JWT 전용 유틸이다.
    private final JwtUtil jwtUtil;

    @Transactional
    // 1) 회원가입을 처리한다.
    // 중복 검사 -> 비밀번호 암호화 -> 엔티티 생성 -> 저장 -> 응답 변환 순서로 진행한다.
    public SignupResponse signup(SignupRequest request) {
        validateDuplicateSignupInfo(request);

        String encodedPassword = encodePassword(request.getPassword());

        Member member = Member.create(
                request.getUserName(),
                encodedPassword,
                request.getLoginId(),
                request.getEmail(),
                request.getNickname()
        );

        Member savedMember = memberRepository.save(member);
        return SignupResponse.from(savedMember);
    }

    // 2) 로그인을 처리한다.
    // 회원 조회 -> 탈퇴 여부 확인 -> 비밀번호 검증 -> 토큰 발급 -> 응답 변환 순서로 진행한다.
    public LoginResponse login(LoginRequest request) {
        Member member = findMemberByLoginId(request.getLoginId());
        validateMemberCanLogin(member);
        validatePassword(request.getPassword(), member.getPassword());

        String accessToken = createAccessToken(member);
        return LoginResponse.of(accessToken, member);
    }

    // 3) 회원가입 시 아이디, 이메일, 닉네임 중복 여부를 검사한다.
    // 지금은 골격 단계이므로 예외는 IllegalArgumentException으로 두고,
    // 이후 커스텀 예외와 전역 예외 처리 단계에서 세분화하면 된다.
    private void validateDuplicateSignupInfo(SignupRequest request) {
        if (memberRepository.existsByLoginId(request.getLoginId())) {
            throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
        }

        if (memberRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        if (memberRepository.existsByNickname(request.getNickname())) {
            throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
        }
    }

    // loginId로 회원을 조회한다.
    // 조회 결과가 없으면 서비스 계층 예외로 전환한다.
    private Member findMemberByLoginId(String loginId) {
        return memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 계정입니다."));
    }

    // 탈퇴한 회원은 로그인하지 못하도록 상태값을 검사한다.
    private void validateMemberCanLogin(Member member) {
        if (member.getStatus() == MemberStatus.WITHDRAWN) {
            throw new IllegalArgumentException("탈퇴된 계정입니다.");
        }
    }

    // 회원가입 시 비밀번호를 암호화해서 저장한다.
    private String encodePassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    // 로그인 시 입력한 비밀번호와 저장된 암호화 비밀번호를 비교한다.
    private void validatePassword(String rawPassword, String encodedPassword) {
        if (!passwordEncoder.matches(rawPassword, encodedPassword)) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
    }

    // 토큰을 만드는 세부 로직은 JwtUtil에 맡긴다.
    // 이렇게 분리하면 AuthService는 로그인 흐름에만 집중할 수 있다.
    private String createAccessToken(Member member) {
        return jwtUtil.createAccessToken(member);
    }
}
