package com.example.myPage;

import com.example.domain.bookmark.entity.FestivalBookmark;
import com.example.domain.bookmark.repository.FestivalBookmarkRepository;
import com.example.domain.festival.entity.Festival;
import com.example.domain.festival.repository.FestivalRepository;
import com.example.domain.member.entity.Member;
import com.example.domain.member.repository.MemberRepository;
import com.example.domain.review.entity.Review;
import com.example.domain.review.repository.ReviewRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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
public class MyPageTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    FestivalRepository festivalRepository;
    @Autowired
    ReviewRepository reviewRepository;
    @Autowired
    FestivalBookmarkRepository festivalBookmarkRepository;



    @Test
    @DisplayName("마이페이지 조회 - 회원 정보와 함께 리뷰/북마크 개수가 정확히 조회된다.")
    @WithMockUser(username = "myPageUser")
    void t1() throws Exception {
        // 1. Given: 테스트용 회원 생성
        Member member = new Member("myPageUser", "1234", "홍길동", "mypage@test.com", "길동이t1", 0);
        memberRepository.save(member);

        // 2. Given: 해당 회원이 작성한 리뷰 2개 생성
        Festival festival = new Festival("F_006", "서울 세계불꽃축제", "설명", "여의도",
                LocalDateTime.now(), LocalDateTime.now().plusDays(1), 126.92, 37.52);
        festivalRepository.save(festival);

        Review review1 = new Review(member, festival, "정말 재밌어요!", null, 5);
        Review review2 = new Review(member, festival, "또 가고 싶네요.", null, 4);
        reviewRepository.saveAll(List.of(review1, review2));

        // 3. Given: 해당 회원이 북마크(찜)한 내역 1개 생성
        // FestivalBookmarkRepository가 member와 festival을 받는다고 가정
        FestivalBookmark bookmark = new FestivalBookmark(member, festival);
        festivalBookmarkRepository.save(bookmark);

        // 4. When: 마이페이지 조회 API 호출
        mockMvc.perform(get("/api/users/me"))
                .andExpect(status().isOk())
                // 5. Then: RsData 구조 및 데이터 검증
                .andExpect(jsonPath("$.resultCode").value("200"))
                .andExpect(jsonPath("$.message").value("마이페이지 조회에 성공하였습니다."))
                .andExpect(jsonPath("$.data.nickname").value("길동이t1"))
                .andExpect(jsonPath("$.data.email").value("mypage@test.com"))
                .andExpect(jsonPath("$.data.reviewCount").value(2)) // 리뷰 2개
                .andExpect(jsonPath("$.data.bookMarkCount").value(1)) // 북마크 1개
                .andDo(print());
    }
}
