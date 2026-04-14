package com.example.domain.member.service;

import com.example.domain.member.dto.request.LoginRequest;
import com.example.domain.member.dto.request.SignupRequest;
import com.example.domain.member.dto.response.LoginResponse;
import com.example.domain.member.dto.response.SignupResponse;
import com.example.domain.member.entity.Member;
import com.example.domain.member.entity.MemberStatus;
import com.example.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
// 인증 관련 비즈니스 로직을 담당하는 서비스다.
// 컨트롤러는 요청을 받고, 실제 회원가입/로그인 처리 순서는 이 서비스가 조합한다.
public class AuthService {

    private final MemberRepository memberRepository;

    @Transactional
    // 회원가입 흐름을 처리한다.
    // 중복 검사 -> 비밀번호 처리 -> Member 생성 -> 저장 -> 응답 DTO 변환 순서로 동작한다.
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

    // 로그인 흐름을 처리한다.
    // 회원 조회 -> 탈퇴 여부 확인 -> 비밀번호 검증 -> 토큰 발급 -> 응답 DTO 변환 순서로 동작한다.
    public LoginResponse login(LoginRequest request) {
        Member member = findMemberByLoginId(request.getLoginId());
        validateMemberCanLogin(member);
        validatePassword(request.getPassword(), member.getPassword());

        String accessToken = createAccessToken(member);
        return LoginResponse.of(accessToken, member);
    }

    // 회원가입 시 loginId, email, nickname 중복 여부를 검사한다.
    // 지금은 골격 단계이므로 예외는 IllegalArgumentException으로 두고,
    // 이후 커스텀 예외와 GlobalExceptionHandler 단계에서 구체화하면 된다.
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
    // Optional을 바로 풀어서, 회원이 없으면 서비스 레벨 예외로 전환한다.
    private Member findMemberByLoginId(String loginId) {
        return memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 계정입니다."));
    }

    // 탈퇴 회원은 로그인하지 못하도록 상태값을 검사한다.
    private void validateMemberCanLogin(Member member) {
        if (member.getStatus() == MemberStatus.WITHDRAWN) {
            throw new IllegalArgumentException("탈퇴된 계정입니다.");
        }
    }

    // 비밀번호 암호화는 다음 단계에서 PasswordEncoder로 연결할 예정이므로
    // 현재는 서비스 골격만 잡기 위해 별도 메서드로 분리한다.
    private String encodePassword(String rawPassword) {
        // TODO: 7단계에서 PasswordEncoder.encode(rawPassword)로 교체
        return rawPassword;
    }

    // 로그인 시 비밀번호 일치 여부를 검사한다.
    // 현재는 골격 단계라 단순 비교로 두고, 다음 단계에서 PasswordEncoder.matches로 교체한다.
    private void validatePassword(String rawPassword, String encodedPassword) {
        // TODO: 7단계에서 PasswordEncoder.matches(rawPassword, encodedPassword)로 교체
        if (!encodedPassword.equals(rawPassword)) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
    }

    // JWT 유틸이 아직 없으므로 토큰 발급 지점을 메서드로 먼저 분리해둔다.
    // 다음 단계에서 JwtTokenProvider 등을 연결하면 login 메서드 본문은 거의 건드리지 않아도 된다.
    private String createAccessToken(Member member) {
        // TODO: 8단계에서 실제 JWT 발급 로직으로 교체
        return "temporary-access-token";
    }
}
