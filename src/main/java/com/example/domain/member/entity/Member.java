package com.example.domain.member.entity;

import com.example.global.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name="member")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntity {

    @Column(nullable = false)
    private String memberName;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String loginId;

    @Email
    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private Integer reportCount = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MemberStatus status = MemberStatus.ACTIVE;

    @Column(nullable = false, unique = true)
    private String nickname;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.USER;

    public Member(String loginId,String  password,String memberName,String email,String nickname,int reportCount){
        this.loginId=loginId;
        this.password=password;
        this.memberName=memberName;
        this.email=email;
        this.nickname=nickname;
        this.reportCount=reportCount;
    }
}
