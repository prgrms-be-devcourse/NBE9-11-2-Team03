package com.example.admin;

import com.example.domain.festival.entity.Festival;
import com.example.domain.festival.repository.FestivalRepository;
import com.example.domain.member.entity.Member;
import com.example.domain.member.repository.MemberRepository;
import com.example.domain.review.entity.Review;
import com.example.domain.review.repository.ReviewRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
public class AdminReviewControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private FestivalRepository festivalRepository;

    @Test
    @DisplayName("신고 누적5회이상인 리뷰 조회")
    public void t1()throws Exception{
        Member author = new Member("user1", "1234", "이름1", "user1@test.com", "작성자", 0);
        memberRepository.save(author);

        Festival festival = new Festival("F_001", "축제", "설명", "주소",
                LocalDateTime.now(), LocalDateTime.now().plusDays(1),
                127.0, 37.0);
        festivalRepository.save(festival);


        Review lowReport = new Review(author, festival, "정상리뷰", null, 5);
        Review midReport = new Review(author, festival, "중간신고", null, 3);
        Review highReport = new Review(author, festival, "고신고리뷰", null, 1);


        ReflectionTestUtils.setField(lowReport, "reportCount", 0);
        ReflectionTestUtils.setField(midReport, "reportCount", 8);
        ReflectionTestUtils.setField(highReport, "reportCount", 15);

        reviewRepository.saveAll(List.of(lowReport, midReport, highReport));

        mockMvc.perform(get("/api/admin/reviews/reported")
                .param("page","0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("200"))
                .andExpect(jsonPath("$.message").value("신고된 리뷰 목록 조회가 완료되었습니다."))
                //신고 5회 미만인 lowReport는 제외되어 총 2개여야 함
                .andExpect(jsonPath("$.data.totalElements").value(2))
                //신고가 15회인 고신고리뷰가 0번리스트
                .andExpect(jsonPath("$.data.content[0].reportCount").value(15))
                .andExpect(jsonPath("$.data.content[0].content").value("고신고리뷰"))
                // 두번째는 중간신고
                .andExpect(jsonPath("$.data.content[1].reportCount").value(8))
                .andDo(print());

    }


}
