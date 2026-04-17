package com.example.festival;

import com.example.domain.festival.entity.Festival;
import com.example.domain.festival.entity.FestivalStatus;
import com.example.domain.festival.repository.FestivalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class FestivalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private FestivalRepository festivalRepository;

    private Festival savedFestival;

    @BeforeEach
    void setUp() {
        // 테스트용 더미 데이터 세팅
        Festival festival = Festival.builder()
                .contentId("FEST-001")
                .overview("축제 상세조회 테스트용 축제입니다.")
                .mapX(126.9780)
                .mapY(37.5665)
                .title("상세조회 타겟 축제")
                .address("서울 테스트구")
                .status(FestivalStatus.ONGOING)
                .startDate(LocalDateTime.now().minusDays(1))
                .endDate(LocalDateTime.now().plusDays(10))
                .viewCount(0)
                .bookMarkCount(0)
                .averageRate(0.0)
                .build();

        savedFestival = festivalRepository.save(festival);
    }

    @Test
    @DisplayName("축제 상세 조회 API - 성공 시 RsData 규격에 맞게 반환되어야 한다")
    void getFestivalDetail_Success() throws Exception {
        // given: 저장된 축제의 ID
        Long targetId = savedFestival.getId();

        // when:  GET /api/festivals/{id} 요청을 보냄
        ResultActions result = mockMvc.perform(get("/api/festivals/{id}", targetId)
                .contentType(MediaType.APPLICATION_JSON));

        // then: 응답 검증 (HTTP 200 OK 인지, RsData 포맷이 맞는지 확인)
        result.andExpect(status().isOk())
                .andDo(print())
                // RsData의 공통 필드 검사
                .andExpect(jsonPath("$.status").value("200"))
                .andExpect(jsonPath("$.message").value("축제 상세 조회 성공"))
                // data 하위의 실제 축제 정보 검사
                .andExpect(jsonPath("$.data.id").value(targetId))
                .andExpect(jsonPath("$.data.title").value("상세조회 타겟 축제"))
                .andExpect(jsonPath("$.data.status").value("ONGOING"));
    }
}
