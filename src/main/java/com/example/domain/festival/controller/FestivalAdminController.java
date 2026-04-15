package com.example.domain.festival.controller;

import com.example.domain.festival.service.FestivalSyncService;
import com.example.domain.festival.dto.response.FestivalSyncResult;
import com.example.global.rsData.RsData;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

//@PreAuthorize("hasRole('ADMIN')") //TODOS: 스프링 시큐리티 적용 후, 설정 적용하기
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/festivals")
public class FestivalAdminController {

    private final FestivalSyncService festivalSyncService;

    //축제 목록 데이터를 수동 동기화한다. (공공 API 목록 조회 -> contentId 기준으로 insert / update 수행)
    @PostMapping("/sync")
    public RsData<FestivalSyncResult> syncFestivals(
            @RequestParam(defaultValue = "1") int pageNo,
            @RequestParam(defaultValue = "100") int numOfRows,
            @RequestParam(defaultValue = "20260101") String eventStartDate
    ) {
        FestivalSyncResult result =
                festivalSyncService.syncFestivalList(pageNo, numOfRows, eventStartDate);

        return RsData.success("축제 목록 동기화가 완료되었습니다.", result);
    }

    //DB에 저장된 모든 축제의 상세 정보를 수동 보강한다 (DB 전체 축제 조회 -> 상세 API 호출 ->  상세 필드 갱신(overview, homepage, contactNumber)
    @PostMapping("/enrich")
    public RsData<FestivalSyncResult> enrichFestivals() {
        FestivalSyncResult result =
                festivalSyncService.enrichFestivalDetails();

        //전체 성공이 아니라 일부 실패가 존재할 수 있으므로 메시지 분기
        String message = result.getFailedCount() > 0
                ? "전체 축제 상세 정보 보강이 부분 완료되었습니다."
                : "전체 축제 상세 정보 보강이 완료되었습니다.";

        return RsData.success(message, result);    }

    //특정 축제 1건만 상세 보강한다.(특정 데이터 재동기화 , 디버깅, 전체 보강 전 검증 목적)
    @PostMapping("/{contentId}/enrich")
    public RsData<Void> enrichFestivalByContentId(@PathVariable String contentId) {
        festivalSyncService.enrichFestivalDetailByContentId(contentId);

        return RsData.success("특정 축제 상세 정보 보강이 완료되었습니다.");
    }
}
