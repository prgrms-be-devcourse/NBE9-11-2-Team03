package com.example.review;

import com.example.domain.festival.entity.Festival;
import com.example.domain.festival.entity.FestivalStatus;
import com.example.domain.festival.repository.FestivalRepository;
import com.example.domain.member.entity.Member;
import com.example.domain.member.entity.Role;
import com.example.domain.member.repository.MemberRepository;
import com.example.domain.review.entity.Review;
import com.example.domain.review.repository.ReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@Transactional
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ReviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private FestivalRepository festivalRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    private Member savedMember;
    private Festival savedFestival;
    private Review savedReview;

    @BeforeEach
    void setUp() {
        savedMember = memberRepository.save(
                new Member(
                        "유저2",
                        "1234",
                        "user2",
                        "user2@test.com",
                        "닉네임2",
                        Role.USER
                )
        );

        savedFestival = festivalRepository.save(
                Festival.builder()
                        .contentId("FEST-001")
                        .overview("리뷰 테스트용 축제")
                        .mapX(126.9780)
                        .mapY(37.5665)
                        .title("리뷰 테스트 축제")
                        .address("서울 테스트구")
                        .status(FestivalStatus.ONGOING)
                        .startDate(LocalDateTime.now().minusDays(1))
                        .endDate(LocalDateTime.now().plusDays(10))
                        .viewCount(0)
                        .bookMarkCount(0)
                        .averageRate(0.0)
                        .build()
        );

        savedReview = reviewRepository.save(
                new Review(
                        savedMember,
                        savedFestival,
                        "삭제 전 리뷰",
                        "https://example.com/review.jpg",
                        5
                )
        );
    }

    @Test
    @DisplayName("리뷰 작성 성공 - 로그인한 사용자가 축제 리뷰를 작성한다.")
    void createReview_success() throws Exception {
        String requestBody = """
                {
                  "content": "리뷰 작성 테스트 내용",
                  "image": "https://example.com/create.jpg",
                  "rating": 4
                }
                """;

        mockMvc.perform(post("/api/festivals/{festivalId}/reviews", savedFestival.getId())
                        .with(user("user2").roles("USER"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(201))
                .andExpect(jsonPath("$.message").value("리뷰 작성이 완료 되었습니다."))
                .andExpect(jsonPath("$.data.festivalId").value(savedFestival.getId()))
                .andExpect(jsonPath("$.data.memberId").value(savedMember.getId()))
                .andExpect(jsonPath("$.data.content").value("리뷰 작성 테스트 내용"))
                .andExpect(jsonPath("$.data.image").value("https://example.com/create.jpg"))
                .andExpect(jsonPath("$.data.rating").value(4))
                .andExpect(jsonPath("$.data.status").value("ACTIVE"));
    }

    @Test
    @DisplayName("리뷰 목록 조회 성공 - 특정 축제의 리뷰 목록을 조회한다.")
    void getReviewList_success() throws Exception {
        reviewRepository.save(
                new Review(
                        savedMember,
                        savedFestival,
                        "두번째 리뷰",
                        "https://example.com/review2.jpg",
                        4
                )
        );

        mockMvc.perform(get("/api/festivals/{festivalId}/reviews", savedFestival.getId())
                        .with(user("user2").roles("USER"))
                        .param("page", "0")
                        .param("size", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("축제 리뷰 목록 조회 성공"))
                .andExpect(jsonPath("$.data.festivalId").value(savedFestival.getId()))
                .andExpect(jsonPath("$.data.page").value(0))
                .andExpect(jsonPath("$.data.size").value(10))
                .andExpect(jsonPath("$.data.totalElements").value(2))
                .andExpect(jsonPath("$.data.content.length()").value(2));
    }

    @Test
    @DisplayName("리뷰 수정 성공 - 본인 리뷰를 수정한다.")
    void updateReview_success() throws Exception {
        String requestBody = """
                {
                  "content": "수정된 리뷰 내용입니다.",
                  "image": "https://example.com/updated.jpg",
                  "rating": 3
                }
                """;

        mockMvc.perform(patch("/api/reviews/{reviewId}", savedReview.getId())
                        .with(user("user2").roles("USER"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("리뷰가 성공적으로 수정되었습니다."))
                .andExpect(jsonPath("$.data.reviewId").value(savedReview.getId()))
                .andExpect(jsonPath("$.data.festivalId").value(savedFestival.getId()))
                .andExpect(jsonPath("$.data.content").value("수정된 리뷰 내용입니다."))
                .andExpect(jsonPath("$.data.image").value("https://example.com/updated.jpg"))
                .andExpect(jsonPath("$.data.rating").value(3));
    }

    @Test
    @DisplayName("리뷰 삭제 성공 - 본인 리뷰를 논리 삭제한다.")
    void deleteReview_success() throws Exception {
        mockMvc.perform(delete("/api/reviews/{reviewId}", savedReview.getId())
                        .with(user("user2").roles("USER"))
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("리뷰 삭제가 완료되었습니다."))
                .andExpect(jsonPath("$.data.reviewId").value(savedReview.getId()))
                .andExpect(jsonPath("$.data.status").value("DELETED"));
    }
}
