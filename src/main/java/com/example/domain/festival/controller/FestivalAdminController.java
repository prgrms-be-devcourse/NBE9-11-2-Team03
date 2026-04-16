package com.example.domain.festival.controller;

import com.example.domain.festival.dto.response.FestivalSyncResponseDto;
import com.example.domain.festival.dto.response.FestivalSyncResult;
import com.example.domain.festival.service.FestivalSyncService;
import com.example.global.rsData.RsData;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@PreAuthorize("hasRole('ADMIN')")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/festivals")
public class FestivalAdminController {

    private final FestivalSyncService festivalSyncService;

    //메인 관리자 동기화 메인 API: 목록 동기화 후, 변경된 contentId만 상세 보강까지 함께 수행한다.
    @PostMapping("/sync-and-enrich")
    public RsData<FestivalSyncResponseDto> syncAndEnrichFestivals(
            @RequestParam(defaultValue = "1") int pageNo,
            @RequestParam(defaultValue = "10") int numOfRows,
            @RequestParam(defaultValue = "20260101") String eventStartDate
    ) {
        FestivalSyncResult listResult =
                festivalSyncService.syncFestivalList(pageNo, numOfRows, eventStartDate);

        List<String> detailTargetContentIds =
                festivalSyncService.collectDetailEnrichTargetContentIds(listResult.getChangedContentIds());

        if (detailTargetContentIds == null || detailTargetContentIds.isEmpty()) {
            FestivalSyncResponseDto response = new FestivalSyncResponseDto(
                    listResult.getTotalCount(),
                    listResult.getCreatedCount(),
                    listResult.getUpdatedCount(),
                    listResult.getFailedCount()
            );

            return RsData.success("축제 동기화가 완료되었고, 상세 보강 대상은 없습니다.", response);
        }

        FestivalSyncResult detailResult =
                festivalSyncService.enrichFestivalDetailsByContentIds(detailTargetContentIds);

        FestivalSyncResponseDto response = new FestivalSyncResponseDto(
                listResult.getTotalCount(),
                listResult.getCreatedCount(),
                listResult.getUpdatedCount() + detailResult.getUpdatedCount(),
                listResult.getFailedCount() + detailResult.getFailedCount()
        );

        String message = detailResult.getFailedCount() > 0
                ? "축제 동기화 및 상세 정보 보강이 부분 완료되었습니다."
                : "축제 동기화 및 상세 정보 보강이 완료되었습니다.";

        return RsData.success(message, response);
    }

    //축제 목록 데이터를 수동 동기화한다. (공공 API 목록 조회 -> contentId 기준으로 insert / 변경사항 확인 후, update 수행)
    //목록만 동기화하는 보조/점검용
    //디버깅용(목록 동기화 OR 상세 보강 문제인지 확인)
    @PostMapping("/sync")
    public RsData<FestivalSyncResponseDto> syncFestivals(
            @RequestParam(defaultValue = "1") int pageNo,
            @RequestParam(defaultValue = "10") int numOfRows,
            @RequestParam(defaultValue = "20260101") String eventStartDate
    ) {
        FestivalSyncResult result =
                festivalSyncService.syncFestivalList(pageNo, numOfRows, eventStartDate);

        FestivalSyncResponseDto response = new FestivalSyncResponseDto(
                result.getTotalCount(),
                result.getCreatedCount(),
                result.getUpdatedCount(),
                result.getFailedCount()
        );

        return RsData.success("축제 목록 동기화가 완료되었습니다.", response);
    }


    //특정 축제 1건만 상세 보강한다.(특정 데이터 재동기화 , 디버깅, 전체 보강 전 검증 목적)
    @PostMapping("/{contentId}/enrich")
    public RsData<Void> enrichFestivalByContentId(@PathVariable String contentId) {
        festivalSyncService.enrichFestivalDetailByContentId(contentId);

        return RsData.success("특정 축제 상세 정보 보강이 완료되었습니다.");
    }
}
