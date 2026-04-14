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

        return new FestivalSyncResult(items.size(), createdCount, updatedCount);
    }


    //상세 API 기반 상세 정보 보강
    public FestivalSyncResult enrichFestivalDetails() {
        List<Festival> festivals = festivalRepository.findAll(); //TODOS: 추후, 페이지 단위 조회/조건 조회 등... 확장 고려

        int updatedCount = 0;

        for (Festival festival : festivals) {
            FestivalApiResponse detailResponse =
                    festivalApiClient.fetchFestivalDetail(festival.getContentId());

            if (detailResponse == null ||
                    detailResponse.getResponse() == null ||
                    detailResponse.getResponse().getHeader() == null ||
                    !"0000".equals(detailResponse.getResponse().getHeader().getResultCode())) {
                continue;
            }

            List<FestivalApiItem> detailItems = detailResponse.getResponse()
                    .getBody()
                    .getItems()
                    .getItem();

            if (detailItems == null || detailItems.isEmpty()) {
                continue;
            }

            FestivalApiItem detailItem = detailItems.get(0);
            festivalApiConverter.updateDetailFields(festival, detailItem);
            updatedCount++;
        }

        return new FestivalSyncResult(festivals.size(), 0, updatedCount);
    }
}

