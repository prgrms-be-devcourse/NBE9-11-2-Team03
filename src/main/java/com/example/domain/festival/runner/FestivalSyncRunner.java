package com.example.domain.festival.runner;

import com.example.domain.festival.FestivalSyncService;
import com.example.domain.festival.dto.response.FestivalSyncResult;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

//실제 공공 API 데이터를 DB에 적재하기 위한 임시 실행 클래스 (테스트 후 삭제 예정)
//실제 운영 시에는 관리자 API 또는 스케줄러로 대체 예정
@Component
@RequiredArgsConstructor
public class FestivalSyncRunner implements CommandLineRunner {

    private final FestivalSyncService festivalSyncService;

    @Override
    public void run(String... args) {
        // 목록 데이터 저장
        FestivalSyncResult listResult =
                festivalSyncService.syncFestivalList(50, 10, "20260101");

        System.out.println("목록 동기화 완료");
        System.out.println("총 조회 건수: " + listResult.getTotalCount());
        System.out.println("신규 저장 건수: " + listResult.getCreatedCount());
        System.out.println("수정 건수: " + listResult.getUpdatedCount());

        /*
        //#####테스트 코드#####################변경 필드만 빠르게 확인
        // 2. 특정 1건만 상세 보강
        //festivalSyncService.enrichFestivalDetailByContentId("3107440");


        // 2. 상세 정보 보강
        FestivalSyncResult detailResult =
                 festivalSyncService.enrichFestivalDetails();

        System.out.println("상세 보강 완료");
        System.out.println("총 대상 건수: " + detailResult.getTotalCount());
        System.out.println("상세 보강 건수: " + detailResult.getUpdatedCount());
        */
    }
}