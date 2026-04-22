package com.example.global.initData;

import com.example.domain.festival.entity.Festival;
import com.example.domain.festival.entity.FestivalStatus;
import com.example.domain.festival.repository.FestivalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Profile("!test")
public class BaseInitData implements ApplicationRunner {

    private final FestivalRepository festivalRepository;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        System.out.println("Initializing dummy festival data...");
        if (festivalRepository.count() > 0) {
            return;
        }

        List<Festival> dummyFestivals = List.of(
                Festival.builder()
                        .contentId("API_001")
                        .title("여의도 봄꽃축제")
                        .overview("서울 여의도에서 열리는 아름다운 벚꽃 축제입니다.")
                        .address("서울특별시 영등포구 여의서로")
                        .startDate(LocalDateTime.now().minusDays(2)) // 이틀 전 시작
                        .endDate(LocalDateTime.now().plusDays(5))    // 5일 뒤 종료
                        .mapX(126.9168)
                        .mapY(37.5273)
                        .status(FestivalStatus.ONGOING)
                        .viewCount(2500)
                        .bookMarkCount(1200)
                        .averageRate(4.8)
                        .lDongRegnCd("11")
                        .build(),

                Festival.builder()
                        .contentId("API_002")
                        .title("가평 자라섬 꽃 축제")
                        .overview("자라섬에서 열리는 향기로운 꽃 축제.")
                        .address("경기도 가평군 자라섬로")
                        .startDate(LocalDateTime.now().plusDays(10)) // 10일 뒤 시작
                        .endDate(LocalDateTime.now().plusDays(20))
                        .mapX(127.5186)
                        .mapY(37.8105)
                        .status(FestivalStatus.UPCOMING)
                        .viewCount(800)
                        .bookMarkCount(150)
                        .averageRate(0.0) // 아직 안 열려서 평점 없음
                        .lDongRegnCd("41")
                        .build(),

                Festival.builder()
                        .contentId("API_003")
                        .title("해운대 모래축제")
                        .overview("부산 해운대 해수욕장의 세계적인 모래 조각 전시.")
                        .address("부산광역시 해운대구 해운대해변로")
                        .startDate(LocalDateTime.now().minusDays(30)) // 한 달 전 시작
                        .endDate(LocalDateTime.now().minusDays(25))   // 이미 종료됨
                        .mapX(129.1586)
                        .mapY(35.1587)
                        .status(FestivalStatus.ENDED)
                        .viewCount(5000)
                        .bookMarkCount(3000)
                        .averageRate(4.5)
                        .lDongRegnCd("26")
                        .build(),

                Festival.builder()
                        .contentId("API_004")
                        .title("수원 화성 문화제")
                        .overview("정조대왕 능행차 등 역사적 의미가 깊은 수원 화성의 축제.")
                        .address("경기도 수원시 팔달구 정조로")
                        .startDate(LocalDateTime.now().minusDays(1))
                        .endDate(LocalDateTime.now().plusDays(3))
                        .mapX(127.0119)
                        .mapY(37.2801)
                        .status(FestivalStatus.ONGOING)
                        .viewCount(1500)
                        .bookMarkCount(900)
                        .averageRate(4.2)
                        .lDongRegnCd("41")
                        .build(),

                Festival.builder()
                        .contentId("API_005")
                        .title("서울 세계 불꽃축제")
                        .overview("가을 밤하늘을 수놓는 한강 여의도의 불꽃놀이.")
                        .address("서울특별시 영등포구 여의동로")
                        .startDate(LocalDateTime.now().plusMonths(5))
                        .endDate(LocalDateTime.now().plusMonths(5).plusDays(1))
                        .mapX(126.9348)
                        .mapY(37.5253)
                        .status(FestivalStatus.UPCOMING)
                        .viewCount(10000) // 시작 전이지만 조회수는 1등 (정렬 테스트용)
                        .bookMarkCount(8000)
                        .averageRate(0.0)
                        .lDongRegnCd("11")
                        .build()
        );

        festivalRepository.saveAll(dummyFestivals);


    }
}
