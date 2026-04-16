//TODOS: 추후, service 디렉터리로 옮기기
package com.example.domain.festival.service;

import com.example.domain.festival.client.FestivalApiClient;
import com.example.domain.festival.converter.FestivalApiConverter;
import com.example.domain.festival.dto.external.FestivalApiItem;
import com.example.domain.festival.dto.external.FestivalApiResponse;
import com.example.domain.festival.dto.response.FestivalSyncResult;
import com.example.domain.festival.entity.Festival;
import com.example.domain.festival.repository.FestivalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class FestivalSyncService {

    private final FestivalApiClient festivalApiClient;
    private final FestivalApiConverter festivalApiConverter;
    private final FestivalRepository festivalRepository;

    // 목록 API 기반 기본 축제 데이터 저장/수정
    public FestivalSyncResult syncFestivalList(int pageNo, int numOfRows, String eventStartDate) {
        FestivalApiResponse response =
                festivalApiClient.fetchFestivalList(pageNo, numOfRows, eventStartDate);

        // 빈 페이지는 예외가 아니라 0건 동기화 결과로 반환(0, ,0, 0, 0)
        if (response == null ||
                response.getResponse() == null ||
                response.getResponse().getBody() == null ||
                response.getResponse().getBody().getItems() == null ||
                response.getResponse().getBody().getItems().getItem() == null ||
                response.getResponse().getBody().getItems().getItem().isEmpty()) {
            return new FestivalSyncResult(0, 0, 0, 0, List.of());
        }

        List<FestivalApiItem> items = response.getResponse()
                .getBody()
                .getItems()
                .getItem();

        int createdCount = 0;
        int updatedCount = 0;
        List<String> changedContentIds = new ArrayList<>();

        for (FestivalApiItem item : items) {
            String contentId = item.getContentid();

            Festival existingFestival = festivalRepository.findByContentId(contentId)
                    .orElse(null);

            if (existingFestival == null) {
                Festival newFestival = festivalApiConverter.toEntityFromListItem(item);
                festivalRepository.save(newFestival);
                createdCount++;
                changedContentIds.add(contentId);
            } else if (festivalApiConverter.hasListChanges(existingFestival, item)) {
                festivalApiConverter.updateFromListItem(existingFestival, item);
                updatedCount++;
                changedContentIds.add(contentId);
            }
        }

        return new FestivalSyncResult(items.size(), createdCount, updatedCount, 0, changedContentIds);
    }


    //상세 보강 대상 contentId 수집 (목록 변경 + 상세 미완료)
    @Transactional(readOnly = true)
    public List<String> collectDetailEnrichTargetContentIds(List<String> changedContentIds) {
        Set<String> targetContentIds = new LinkedHashSet<>(changedContentIds);

        List<Festival> festivals = festivalRepository.findAll();

        for (Festival festival : festivals) {
            if (festivalApiConverter.isDetailIncomplete(festival)) {
                targetContentIds.add(festival.getContentId());
            }
        }

        return new ArrayList<>(targetContentIds);
    }

    //상세 API 기반 상세 정보 보강 (변경된 contentId 목록만 변경 대상 ex. 초기적재 or 실제 변경)
    public FestivalSyncResult enrichFestivalDetailsByContentIds(List<String> contentIds) {
        int updatedCount = 0;
        int failedCount = 0;

        for (String contentId : contentIds) {
            try {
                Festival festival = festivalRepository.findByContentId(contentId)
                        .orElseThrow(() -> new NoSuchElementException(
                                "해당 contentId의 축제를 찾을 수 없습니다. contentId=" + contentId));

                boolean wasDetailIncomplete = festivalApiConverter.isDetailIncomplete(festival);

                FestivalApiResponse detailResponse =
                        festivalApiClient.fetchFestivalDetail(contentId);

                if (detailResponse == null ||
                        detailResponse.getResponse() == null ||
                        detailResponse.getResponse().getHeader() == null ||
                        !"0000".equals(detailResponse.getResponse().getHeader().getResultCode())) {
                    failedCount++;
                    continue;
                }

                if (detailResponse.getResponse().getBody() == null ||
                        detailResponse.getResponse().getBody().getItems() == null ||
                        detailResponse.getResponse().getBody().getItems().getItem() == null) {
                    failedCount++;
                    continue;
                }

                List<FestivalApiItem> detailItems = detailResponse.getResponse()
                        .getBody()
                        .getItems()
                        .getItem();

                if (detailItems.isEmpty()) {
                    failedCount++;
                    continue;
                }

                FestivalApiItem detailItem = detailItems.get(0);

                //상세 정보도 실제 변경된 경우에만 update 수행
                if (wasDetailIncomplete || festivalApiConverter.hasDetailChanges(festival, detailItem)) {
                    festivalApiConverter.updateDetailFields(festival, detailItem);

                    if (!wasDetailIncomplete) {
                        updatedCount++;
                    }
                }

            } catch (Exception e) {
                System.out.println("변경 축제 상세 보강 중 실패 contentId=" + contentId
                        + ", message=" + e.getMessage());
                failedCount++;
            }
        }

        return new FestivalSyncResult(contentIds.size(), 0, updatedCount, failedCount, contentIds);
    }

    //상세 API 기반 상세 정보 보강 (특정 축제 1건에 대해한 상세 정보를 보강한다)
    //특정 데이터에 문제가 발생했을 때 부분 재동기화 용도로 활용, API 호출을 1건만 수행하므로 rate limit(429) 부담이 적음
    @Transactional
    public void enrichFestivalDetailByContentId(String contentId) {
        Festival festival = festivalRepository.findByContentId(contentId)
                .orElseThrow(() -> new NoSuchElementException(
                        "해당 contentId의 축제를 찾을 수 없습니다. contentId=" + contentId));

        FestivalApiResponse response =
                festivalApiClient.fetchFestivalDetail(contentId);

        if (response == null ||
                response.getResponse() == null ||
                response.getResponse().getHeader() == null ||
                !"0000".equals(response.getResponse().getHeader().getResultCode())) {
            return;
        }

        if (response.getResponse().getBody() == null ||
                response.getResponse().getBody().getItems() == null ||
                response.getResponse().getBody().getItems().getItem() == null) {
            return;
        }

        List<FestivalApiItem> items = response.getResponse()
                .getBody()
                .getItems()
                .getItem();

        if (items.isEmpty()) {
            return;
        }

        FestivalApiItem detailItem = items.get(0);

        if (festivalApiConverter.hasDetailChanges(festival, detailItem)) {
            festivalApiConverter.updateDetailFields(festival, detailItem);
        }
    }
}

