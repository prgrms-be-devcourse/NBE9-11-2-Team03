//TODOS: 추후, service 디렉터리로 옮기기
package com.example.domain.festival;

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

import java.util.List;
import java.util.NoSuchElementException;

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
            return new FestivalSyncResult(0, 0, 0, 0);
        }

        List<FestivalApiItem> items = response.getResponse()
                .getBody()
                .getItems()
                .getItem();

        int createdCount = 0;
        int updatedCount = 0;

        for (FestivalApiItem item : items) {
            String contentId = item.getContentid();

            Festival existingFestival = festivalRepository.findByContentId(contentId)
                    .orElse(null);

            if (existingFestival == null) {
                Festival newFestival = festivalApiConverter.toEntityFromListItem(item);
                festivalRepository.save(newFestival);
                createdCount++;
            } else {
                festivalApiConverter.updateFromListItem(existingFestival, item);
                updatedCount++;
            }
        }

        return new FestivalSyncResult(items.size(), createdCount, updatedCount, 0);
    }


    //상세 API 기반 상세 정보 보강
    public FestivalSyncResult enrichFestivalDetails() {
        List<Festival> festivals = festivalRepository.findAll(); //TODOS: API 호출량에 따른 rate limit 고려 필요 => 추후, 페이지 단위 조회/조건 조회 등... 확장 고려

        int updatedCount = 0;
        int failedCount = 0;

        for (Festival festival : festivals) {
            try {
                FestivalApiResponse detailResponse =
                        festivalApiClient.fetchFestivalDetail(festival.getContentId());

                if (detailResponse == null ||
                        detailResponse.getResponse() == null ||
                        detailResponse.getResponse().getHeader() == null ||
                        !"0000".equals(detailResponse.getResponse().getHeader().getResultCode())) {
                    failedCount++; //상세 응답 자체가 비정상이면 실패 건수 증가
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
                    failedCount++; //상세 item이 비어 있는 경우도 실패로 간주
                    continue;
                }

                FestivalApiItem detailItem = detailItems.get(0);
                festivalApiConverter.updateDetailFields(festival, detailItem);
                updatedCount++;

            } catch (Exception e) {
                //TODOS: 현재는 실패한 1건만 건너뛰고 계속 진행중
                //       추후 logger 적용 및 실패 건수 별도 관리 확장 고려
                System.out.println("전체 상세 보강 중 실패 contentId=" + festival.getContentId()
                        + ", message=" + e.getMessage());
                failedCount++;
                continue;
            }
        }

        return new FestivalSyncResult(festivals.size(), 0, updatedCount, failedCount);
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

        festivalApiConverter.updateDetailFields(festival, items.get(0));
    }

}

