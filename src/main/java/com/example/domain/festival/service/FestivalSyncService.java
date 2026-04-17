
package com.example.domain.festival.service;

import com.example.domain.festival.client.FestivalApiClient;
import com.example.domain.festival.converter.FestivalApiConverter;
import com.example.domain.festival.dto.external.FestivalApiItem;
import com.example.domain.festival.dto.external.FestivalApiResponse;
import com.example.domain.festival.dto.response.FestivalSyncResult;
import com.example.domain.festival.entity.Festival;
import com.example.domain.festival.event.FestivalSyncEventPublisher;
import com.example.domain.festival.repository.FestivalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class FestivalSyncService {

    private final FestivalApiClient festivalApiClient;
    private final FestivalApiConverter festivalApiConverter;
    private final FestivalRepository festivalRepository;
    private final FestivalSyncEventPublisher festivalSyncEventPublisher;

    // 목록 API 기반 기본 축제 데이터 저장/수정
    public FestivalSyncResult syncFestivalList(int pageNo, int numOfRows, String eventStartDate) {

        //성능 TEST코드: API 시간 호출 시간 (추후 삭제 가능)
        long totalStart = System.currentTimeMillis();
        long apiStart = System.currentTimeMillis();

        FestivalApiResponse response =
                festivalApiClient.fetchFestivalList(pageNo, numOfRows, eventStartDate);

        //성능 TEST코드: API 시간 호출 시간 (추후 삭제 가능)
        long apiEnd = System.currentTimeMillis();

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

        //성능TEST코드: DB 처리 시간 시간 (추후 삭제 가능)
        long dbStart = System.currentTimeMillis();

        // 목록 API 응답에서 contentId만 먼저 추출한다. (DB를 건별 조회X, 필요한 축제만 한 번에 조회하기 위함)
        List<String> contentIds = items.stream()
                .map(FestivalApiItem::getContentid)
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        // contentId 목록으로 기존 축제를 한 번에 조회한다.
        List<Festival> existingFestivals = festivalRepository.findAllByContentIdIn(contentIds);

        // 조회한 축제를 contentId 기준 Map으로 변환한다. (item 순회 시 O(1)에 가깝게 기존 축제를 찾기 위함)
        Map<String, Festival> existingFestivalMap = existingFestivals.stream()
                .collect(Collectors.toMap(
                        Festival::getContentId,
                        Function.identity()
                ));

        //비교 저장 로직
        for (FestivalApiItem item : items) {
            String contentId = item.getContentid();

            // 미리 조회한 Map에서 기존 데이터를 꺼내서 사용
            Festival existingFestival = existingFestivalMap.get(contentId);

            // DB에 없는 신규 축제 → insert
            if (existingFestival == null) {
                Festival newFestival = festivalApiConverter.toEntityFromListItem(item);
                festivalRepository.save(newFestival);
                createdCount++;
                changedContentIds.add(contentId);
            }
            // DB에 존재하고 목록 필드가 변경된 경우 → update
            else if (festivalApiConverter.hasListChanges(existingFestival, item)) {
                festivalApiConverter.updateFromListItem(existingFestival, item);
                updatedCount++;
                changedContentIds.add(contentId);
            }
        }

        //성능TEST코드: API 시간 호출 시간 (추후 삭제 가능)
        long dbEnd = System.currentTimeMillis();
        long totalEnd = System.currentTimeMillis();

        System.out.println("목록 API 시간: " + (apiEnd - apiStart) + "ms");
        System.out.println("DB 처리 시간: " + (dbEnd - dbStart) + "ms");
        System.out.println("총 시간: " + (totalEnd - totalStart) + "ms");

        return new FestivalSyncResult(items.size(), createdCount, updatedCount, 0, changedContentIds);
    }


    //목록 동기화 완료 후, 변경된 contentId 목록에 대한 상세 보강 이벤트를 발행함
    public void publishSyncCompletedEvent(List<String> changedContentIds) {
        if (changedContentIds == null || changedContentIds.isEmpty()) {
            return;
        }

        festivalSyncEventPublisher.publishSyncCompleted(changedContentIds);
    }

    //####################삭제 고려#########이제 배열 값 처리x => 이벤트 처리라?##
    //상세 보강 대상 contentId 수집 (목록 변경 + 상세 미완료)
    @Transactional(readOnly = true)
    public List<String> collectDetailEnrichTargetContentIds(List<String> changedContentIds) {
        Set<String> targetContentIds = new LinkedHashSet<>(changedContentIds);

        //성능TEST코드: API 시간 호출 시간 (추후 삭제 가능)
        long start = System.currentTimeMillis();

        List<Festival> festivals = festivalRepository.findAll();

        //성능TEST코드: API 시간 호출 시간 (추후 삭제 가능)
        long end = System.currentTimeMillis();
        System.out.println("findAll 조회 시간: " + (end - start) + "ms");

        for (Festival festival : festivals) {
            if (festivalApiConverter.isDetailIncomplete(festival)) {
                targetContentIds.add(festival.getContentId());
            }
        }

        //상세 Default 확인용////
        System.out.println("상세 보강 대상 수: " + targetContentIds.size());
        System.out.println("상세 보강 대상 contentIds: " + targetContentIds);

        return new ArrayList<>(targetContentIds);
    }


    //상세 API 기반 상세 정보 보강 (변경된 contentId 목록만 변경 대상 ex. 초기적재 or 실제 변경)
    public FestivalSyncResult enrichFestivalDetailsByContentIds(List<String> contentIds) {
        int updatedCount = 0;
        int failedCount = 0;

        //성능TEST코드: API 시간 호출 시간 (추후 삭제 가능)
        long totalStart = System.currentTimeMillis();
        //성능TEST코드: 상세 API 호출 횟수 (추후 삭제 가능)
        int apiCallCount = 0;

        for (String contentId : contentIds) {
            try {
                Festival festival = festivalRepository.findByContentId(contentId)
                        .orElseThrow(() -> new NoSuchElementException(
                                "해당 contentId의 축제를 찾을 수 없습니다. contentId=" + contentId));

                boolean wasDetailIncomplete = festivalApiConverter.isDetailIncomplete(festival);

                //성능TEST코드: API 시간 호출 시간 및 호출 횟수 (추후 삭제 가능)
                long apiStart = System.currentTimeMillis();
                apiCallCount++;

                FestivalApiResponse detailResponse =
                        festivalApiClient.fetchFestivalDetail(contentId);

                //성능TEST코드: API 시간 호출 시간 (추후 삭제 가능)
                long apiEnd = System.currentTimeMillis();
                System.out.println("상세 API 1건: " + (apiEnd - apiStart) + "ms");


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

        //성능TEST코드: API 시간 호출 시간 (추후 삭제 가능)
        long totalEnd = System.currentTimeMillis();
        System.out.println("상세 API 호출 횟수: " + apiCallCount);
        System.out.println("상세 전체 시간: " + (totalEnd - totalStart) + "ms");

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

