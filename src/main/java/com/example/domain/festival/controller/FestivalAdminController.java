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

    // 메인 관리자 동기화 API
    // 목록 데이터를 먼저 동기화한 뒤, 변경된 contentId에 대해 상세 보강 후속 처리를 위한 이벤트를 발행함
    // 본 응답에는 목록 동기화 결과만 포함되며, 상세 보강 완료 결과는 포함되지 않음
    @PostMapping("/sync-and-enrich")
    public RsData<FestivalSyncResponseDto> syncAndEnrichFestivals(
            @RequestParam(defaultValue = "1") int pageNo,
            @RequestParam(defaultValue = "200") int numOfRows,
            @RequestParam(defaultValue = "20260101") String eventStartDate
    ) {
        FestivalSyncResult listResult =
                festivalSyncService.syncFestivalList(pageNo, numOfRows, eventStartDate);

        //목록 동기화 결과를 바탕으로 상세 보강 이벤트 발행
        festivalSyncService.publishSyncCompletedEvent(listResult.getChangedContentIds());

        FestivalSyncResponseDto response = new FestivalSyncResponseDto(
                listResult.getTotalCount(),
                listResult.getCreatedCount(),
                listResult.getUpdatedCount(),
                listResult.getFailedCount()
        );

        boolean hasFailedItems = listResult.getFailedCount() > 0;
        boolean hasDetailTargets = listResult.getChangedContentIds() != null
                && !listResult.getChangedContentIds().isEmpty();

        String message;

        if (!hasFailedItems && !hasDetailTargets) {
            message = "축제 목록 동기화가 완료되었고, 상세 보강 대상은 없습니다.";
        } else if (!hasFailedItems) {
            message = "축제 목록 동기화가 완료되었고, 변경된 축제에 대한 상세 보강이 후속 처리됩니다.";
        } else if (!hasDetailTargets) {
            message = "축제 목록 동기화가 부분 완료되었습니다. 일부 축제 목록은 처리되지 않았으며, 상세 보강 대상은 없습니다.";
        } else {
            message = "축제 목록 동기화가 부분 완료되었습니다. 일부 축제 목록은 처리되지 않았으며, 변경된 축제에 대한 상세 보강이 후속 처리됩니다.";
        }
        return RsData.success(message, response);
    }

    //축제 목록 데이터를 수동 동기화한다. (공공 API 목록 조회 -> contentId 기준으로 insert / 변경사항 확인 후, update 수행)
    //목록만 동기화하는 보조/점검용
    //디버깅용(목록 동기화 OR 상세 보강 문제인지 확인)
    @PostMapping("/sync-list")
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

    //축제 상세정보 전체를 보강한다. (외부 API 호출 제한으로 인해, 상세정보가 보강이 되지 않았을 때, 축제 상세 정보 재동기화 목적)
    @PostMapping("/enrich-all")
    public RsData<FestivalSyncResponseDto> enrichAllFestivalDetails() {
        List<String> targetContentIds =
                festivalSyncService.collectDetailEnrichTargetContentIds(List.of());

        if (targetContentIds.isEmpty()) {
            return RsData.success(
                    "상세 보강 대상 축제가 없습니다.",
                    new FestivalSyncResponseDto(0, 0, 0, 0)
            );
        }

        FestivalSyncResult result =
                festivalSyncService.enrichFestivalDetailsByContentIds(targetContentIds);

        FestivalSyncResponseDto response = new FestivalSyncResponseDto(
                result.getTotalCount(),
                result.getCreatedCount(),
                result.getUpdatedCount(),
                result.getFailedCount()
        );

        String message;

        if (result.getUpdatedCount() == 0 && result.getFailedCount() > 0) {
            message = "축제 상세 보강이 실패했습니다. 외부 API 제한 또는 오류로 인해 처리되지 않았습니다.";
        } else if (result.getFailedCount() > 0) {
            message = "축제 상세 보강이 부분 완료되었습니다. 일부 대상은 외부 API 제한 또는 오류로 처리되지 않았습니다.";
        } else {
            message = "축제 상세 보강이 완료되었습니다.";
        }

        return RsData.success(message, response);
    }

    //특정 축제 1건만 상세 보강한다.(특정 데이터 재동기화 , 디버깅, 전체 보강 전 검증 목적)
    @PostMapping("/{contentId}/enrich")
    public RsData<Void> enrichFestivalByContentId(@PathVariable String contentId) {
        festivalSyncService.enrichFestivalDetailByContentId(contentId);

        return RsData.success("특정 축제 상세 정보 보강이 완료되었습니다.");
    }
}
