package com.example.domain.member.entity;

import com.example.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "member")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
// 회원이 어떤 값을 가지는지 정의하는 회원 엔티티
public class Member extends BaseEntity {

    // DB 컬럼과 자바 필드 연결
    @Column(name = "user_name", nullable = false)
    private String userName;

    @Column(nullable = false)
    private String password;

    @Column(name = "login_id", nullable = false, unique = true)
    private String loginId;

    @Email
    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "report_count", nullable = false)
    private Integer reportCount = 0;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MemberStatus status = MemberStatus.ACTIVE;

    @Column(nullable = false, unique = true)
    private String nickname;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.USER;

    private Member(
            String userName,
            String password,
            String loginId,
            String email,
            String nickname,
            Integer reportCount,
            MemberStatus status,
            Role role
    ) {
        this.userName = userName;
        this.password = password;
        this.loginId = loginId;
        this.email = email;
        this.nickname = nickname;
        this.reportCount = reportCount;
        this.status = status;
        this.role = role;
    }

    // 회원가입 시 기본 status, role, reportCount를 한곳에서 설정한다.
    public static Member create(
            String userName,
            String password,
            String loginId,
            String email,
            String nickname
    ) {
        return new Member(
                userName,
                password,
                loginId,
                email,
                nickname,
                0,
                MemberStatus.ACTIVE,
                Role.USER
        );
    }

    // 회원탈퇴를 삭제 방식으로 처리할 준비
    public void withdraw() {
        this.status = MemberStatus.WITHDRAWN;
    }
}
