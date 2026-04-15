package com.example.global.init;

import com.example.domain.festival.entity.Festival;
import com.example.domain.festival.repository.FestivalRepository;
import com.example.domain.member.entity.Member;
import com.example.domain.member.entity.Role;
import com.example.domain.member.repository.MemberRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

//초기 데이터용 추후 삭제
@Configuration
public class InitDataConfig {

    @Bean
    public CommandLineRunner initData(
            MemberRepository memberRepository,
            FestivalRepository festivalRepository,
            // 초기 회원도 일반 회원가입과 똑같이 암호화된 비밀번호를 저장해야 한다.
            PasswordEncoder passwordEncoder
    ) {
        return args -> {

            //  APi 확인용 Member 데이터
            if (memberRepository.count() == 0) {
                Member member = new Member(
                        "테스트회원",
                        // 로그인 검증에서 matches()를 사용하므로 DB에는 암호화된 값이 들어가야 한다.
                        passwordEncoder.encode("1234"),
                        "test01",
                        "test01@example.com",
                        "축제왕",
                        Role.USER
                );
                memberRepository.save(member);
            }

            // APi 확인용 Festival 데이터
            if (festivalRepository.count() == 0) {
                Festival festival = new Festival(
                        "FESTIVAL_001",                        // contentId (유니크)
                        "서울 봄꽃 축제",                       // title
                        "서울에서 열리는 봄꽃 축제입니다.",        // overview
                        "서울시 중구 서울광장",                   // address
                        LocalDateTime.now(),                   // 시작일
                        LocalDateTime.now().plusDays(7),       // 종료일
                        126.9780,                              // 경도
                        37.5665                                // 위도
                );

                festivalRepository.save(festival);
            }
        };
    }
}
