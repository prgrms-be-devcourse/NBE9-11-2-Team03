package com.example.domain.member.repository;

import com.example.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    //로그인 시 회원 조회
    Optional<Member> findByLoginId(String loginId);

    //회원가입할 때 loginId 중복 확인
    boolean existsByLoginId(String loginId);

    //회원가입할 때 이메일 중복 검사
    boolean existsByEmail(String email);

    //회원가입할 때 닉네임 중복 검사
    boolean existsByNickname(String nickname);
}
