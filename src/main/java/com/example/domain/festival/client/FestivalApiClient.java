package com.example.domain.festival.client;


import com.example.domain.festival.dto.external.FestivalApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

//공공 축제 API 호출 전용 클래스
@Component
@RequiredArgsConstructor
public class FestivalApiClient {

    //HTTP 요청을 보내기 위한 스프링 제공 객체
    private final RestTemplate restTemplate;

    //application.yaml 파일에 공공 API 인증키 및 base URL을 저장해야함
    @Value("${api.public-data.key}")
    private String serviceKey;

    @Value("${api.public-data.base-url}")
    private String baseUrl;

    //축제 목록 조회 API 호출
    public FestivalApiResponse fetchFestivalList(int pageNo, int numOfRows, String eventStartDate) {
        URI uri = UriComponentsBuilder
                .fromUriString(baseUrl)
                .path("/searchFestival2")
                .queryParam("serviceKey", serviceKey)
                .queryParam("MobileOS", "ETC")
                .queryParam("MobileApp", "TestApp")
                .queryParam("_type", "json")
                .queryParam("pageNo", pageNo)
                .queryParam("numOfRows", numOfRows)
                .queryParam("eventStartDate", eventStartDate)
                .build(true) // 인코딩 유지
                .toUri();

        return restTemplate.getForObject(uri, FestivalApiResponse.class);
    }


    //축제 상세 조회 API 호출
    public FestivalApiResponse fetchFestivalDetail(String contentId) {

        URI uri = UriComponentsBuilder
                .fromUriString(baseUrl)
                .path("/detailCommon2")
                .queryParam("MobileOS", "ETC")
                .queryParam("MobileApp", "TestApp")
                .queryParam("_type", "json")
                .queryParam("contentId", contentId)
                .queryParam("serviceKey", serviceKey)
                .build(true)
                .toUri();

        return restTemplate.getForObject(uri, FestivalApiResponse.class);
    }
}