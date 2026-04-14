package com.example.admin;

import com.example.domain.member.entity.Member;
import com.example.domain.member.entity.MemberStatus;
import com.example.domain.member.repository.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
public class AdminMemberControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private MemberRepository memberRepository;
    
    @Test
    @DisplayName("관리자 전체회원 목록조회")
    void t1() throws Exception{
        mockMvc.perform(get("/api/admin/members")
                .param("page","0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("200"))
                .andExpect(jsonPath("$.message").value("회원 목록 조회 성공"))
                .andDo(print());
    }
    @Test
    @DisplayName("신고순 정렬시 가장 많이 신고받은 회원이 첫번째로 노출 된다.")
    void t2() throws Exception{
        Member lowReport = new Member("user1", "1234","이름1","user1@test.com", "저신고자", 1);
        Member midReport = new Member("user2", "1234","이름2","user2@test.com", "중신고자", 5);
        Member highReport = new Member("user3", "1234","이름3","user3@test.com", "고신고자", 10);
        memberRepository.saveAll(List.of(lowReport, midReport, highReport));

        mockMvc.perform(get("/api/admin/members/reported")
                        .param("page", "0")
                        .param("sort", "reportCount,desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].nickname").value("고신고자"))
                .andExpect(jsonPath("$.data.content[0].reportCount").value(10))
                .andExpect(jsonPath("$.data.content[1].nickname").value("중신고자"))
                .andDo(print());
    }

    
}
