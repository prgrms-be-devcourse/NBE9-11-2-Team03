package com.example.domain.festival.client;

import com.example.domain.festival.dto.external.FestivalApiResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class FestivalApiClientTest {
    //목적. FestivalApiClient 실제 호출 테스트(공공 API가 정상 호출되는지, 응답이 DTO로 잘 매핑되는지)
    @Autowired
    private FestivalApiClient festivalApiClient;

    @Test
    @DisplayName("공공 API 축제 목록 조회 테스트")
    void fetch_festival_list_test() {
        FestivalApiResponse response = festivalApiClient.fetchFestivalList(1, 10, "20260101");

        assertThat(response).isNotNull();
        assertThat(response.getResponse()).isNotNull();
        assertThat(response.getResponse().getHeader()).isNotNull();
        assertThat(response.getResponse().getHeader().getResultCode()).isEqualTo("0000");
        assertThat(response.getResponse().getBody()).isNotNull();
        assertThat(response.getResponse().getBody().getItems()).isNotNull();
        assertThat(response.getResponse().getBody().getItems().getItem()).isNotEmpty();
    }

    @Test
    @DisplayName("공공 API 축제 상세 조회 테스트")
    void fetch_festival_detail_test() {

        FestivalApiResponse response =
                festivalApiClient.fetchFestivalDetail("694576");

        assertThat(response).isNotNull();
        assertThat(response.getResponse().getHeader().getResultCode()).isEqualTo("0000");

        assertThat(response.getResponse().getBody().getItems().getItem()).isNotEmpty();

        // 핵심 필드 확인
        var item = response.getResponse().getBody().getItems().getItem().get(0);

        assertThat(item.getOverview()).isNotNull();
        assertThat(item.getHomepage()).isNotNull();
    }
}
