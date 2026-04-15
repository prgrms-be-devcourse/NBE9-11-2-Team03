package com.example.domain.festival.service;

import com.example.domain.festival.client.FestivalApiClient;
import com.example.domain.festival.converter.FestivalApiConverter;
import com.example.domain.festival.dto.external.*;
import com.example.domain.festival.dto.response.FestivalSyncResult;
import com.example.domain.festival.entity.Festival;
import com.example.domain.festival.entity.FestivalStatus;
import com.example.domain.festival.repository.FestivalRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class FestivalSyncServiceTest {
    // 목적: mock 기반 서비스 흐름 검증 테스트

    private final FestivalApiClient festivalApiClient = mock(FestivalApiClient.class);
    private final FestivalApiConverter festivalApiConverter = mock(FestivalApiConverter.class);
    private final FestivalRepository festivalRepository = mock(FestivalRepository.class);

    private final FestivalSyncService festivalSyncService =
            new FestivalSyncService(festivalApiClient, festivalApiConverter, festivalRepository);

    @Nested
    @DisplayName("목록 동기화 테스트")
    class SyncFestivalListTest {

        @Test
        @DisplayName("신규 축제면 저장한다")
        void syncFestivalList_create_test() throws Exception {
            // given
            FestivalApiItem item = createApiItem("1001", "가야문화축제");
            FestivalApiResponse response = createResponse(List.of(item));

            Festival newFestival = Festival.builder()
                    .contentId("1001")
                    .title("가야문화축제")
                    .overview("상세 설명 없음")
                    .contactNumber("055-330-6840")
                    .firstImageUrl("image1.jpg")
                    .thumbnailUrl("image2.jpg")
                    .address("경상남도 김해시 대성동")
                    .homepageUrl("https://test.com")
                    .startDate(LocalDateTime.of(2026, 4, 30, 0, 0))
                    .endDate(LocalDateTime.of(2026, 5, 3, 23, 59, 59))
                    .mapX(128.87)
                    .mapY(35.23)
                    .lDongRegnCd("48")
                    .status(FestivalStatus.UPCOMING)
                    .build();

            when(festivalApiClient.fetchFestivalList(1, 10, "20260101")).thenReturn(response);
            when(festivalRepository.findByContentId("1001")).thenReturn(Optional.empty());
            when(festivalApiConverter.toEntityFromListItem(item)).thenReturn(newFestival);

            // when
            FestivalSyncResult result = festivalSyncService.syncFestivalList(1, 10, "20260101");

            // then
            assertThat(result.getTotalCount()).isEqualTo(1);
            assertThat(result.getCreatedCount()).isEqualTo(1);
            assertThat(result.getUpdatedCount()).isEqualTo(0);

            verify(festivalRepository, times(1)).save(newFestival);
            verify(festivalApiConverter, times(1)).toEntityFromListItem(item);
        }

        @Test
        @DisplayName("기존 축제면서 목록 정보가 변경된 경우 수정한다")
        void syncFestivalList_update_test() throws Exception {
            // given
            FestivalApiItem item = createApiItem("1001", "수정된 축제명");
            FestivalApiResponse response = createResponse(List.of(item));

            Festival existingFestival = Festival.builder()
                    .contentId("1001")
                    .title("기존 축제명")
                    .overview("기존 설명")
                    .contactNumber("055-1111-1111")
                    .firstImageUrl("old1.jpg")
                    .thumbnailUrl("old2.jpg")
                    .address("기존 주소")
                    .homepageUrl("https://old.com")
                    .startDate(LocalDateTime.of(2026, 4, 1, 0, 0))
                    .endDate(LocalDateTime.of(2026, 4, 2, 23, 59, 59))
                    .mapX(127.0)
                    .mapY(37.0)
                    .lDongRegnCd("11")
                    .status(FestivalStatus.UPCOMING)
                    .build();

            when(festivalApiClient.fetchFestivalList(1, 10, "20260101")).thenReturn(response);
            when(festivalRepository.findByContentId("1001")).thenReturn(Optional.of(existingFestival));
            when(festivalApiConverter.hasListChanges(existingFestival, item)).thenReturn(true);

            // when
            FestivalSyncResult result = festivalSyncService.syncFestivalList(1, 10, "20260101");

            // then
            assertThat(result.getTotalCount()).isEqualTo(1);
            assertThat(result.getCreatedCount()).isEqualTo(0);
            assertThat(result.getUpdatedCount()).isEqualTo(1);

            verify(festivalApiConverter, times(1)).hasListChanges(existingFestival, item);
            verify(festivalApiConverter, times(1)).updateFromListItem(existingFestival, item);
            verify(festivalRepository, never()).save(any(Festival.class));
        }

        @Test
        @DisplayName("기존 축제지만 목록 정보가 변경되지 않은 경우 수정하지 않는다")
        void syncFestivalList_no_change_test() throws Exception {
            // given
            FestivalApiItem item = createApiItem("1001", "기존 축제명");
            FestivalApiResponse response = createResponse(List.of(item));

            Festival existingFestival = Festival.builder()
                    .contentId("1001")
                    .title("기존 축제명")
                    .overview("기존 설명")
                    .contactNumber("055-1111-1111")
                    .firstImageUrl("old1.jpg")
                    .thumbnailUrl("old2.jpg")
                    .address("기존 주소")
                    .homepageUrl("https://old.com")
                    .startDate(LocalDateTime.of(2026, 4, 1, 0, 0))
                    .endDate(LocalDateTime.of(2026, 4, 2, 23, 59, 59))
                    .mapX(127.0)
                    .mapY(37.0)
                    .lDongRegnCd("11")
                    .status(FestivalStatus.UPCOMING)
                    .build();

            when(festivalApiClient.fetchFestivalList(1, 10, "20260101")).thenReturn(response);
            when(festivalRepository.findByContentId("1001")).thenReturn(Optional.of(existingFestival));
            when(festivalApiConverter.hasListChanges(existingFestival, item)).thenReturn(false);

            // when
            FestivalSyncResult result = festivalSyncService.syncFestivalList(1, 10, "20260101");

            // then
            assertThat(result.getTotalCount()).isEqualTo(1);
            assertThat(result.getCreatedCount()).isEqualTo(0);
            assertThat(result.getUpdatedCount()).isEqualTo(0);

            verify(festivalApiConverter, times(1)).hasListChanges(existingFestival, item);
            verify(festivalApiConverter, never()).updateFromListItem(any(), any());
            verify(festivalRepository, never()).save(any(Festival.class));
        }
    }

    @Nested
    @DisplayName("상세 보강 테스트")
    class EnrichFestivalDetailsByContentIdsTest {

        @Test
        @DisplayName("상세 API 성공 응답이고 상세 정보가 변경된 경우 보강한다")
        void enrichFestivalDetailsByContentIds_success_test() throws Exception {
            // given
            Festival festival = Festival.builder()
                    .contentId("694576")
                    .title("가야문화축제")
                    .overview("상세 설명 없음")
                    .contactNumber(null)
                    .firstImageUrl("image1.jpg")
                    .thumbnailUrl("image2.jpg")
                    .address("경상남도 김해시 대성동")
                    .homepageUrl(null)
                    .startDate(LocalDateTime.of(2026, 4, 30, 0, 0))
                    .endDate(LocalDateTime.of(2026, 5, 3, 23, 59, 59))
                    .mapX(128.87)
                    .mapY(35.23)
                    .lDongRegnCd("48")
                    .status(FestivalStatus.UPCOMING)
                    .build();

            FestivalApiItem detailItem = createApiItem("694576", "가야문화축제");
            setField(detailItem, "overview", "상세 설명");
            setField(detailItem, "homepage", "https://gcfkorea.com/");
            setField(detailItem, "tel", "055-330-6840");

            FestivalApiResponse detailResponse = createResponse(List.of(detailItem));

            when(festivalRepository.findByContentId("694576")).thenReturn(Optional.of(festival));
            when(festivalApiClient.fetchFestivalDetail("694576")).thenReturn(detailResponse);
            when(festivalApiConverter.hasDetailChanges(festival, detailItem)).thenReturn(true);

            // when
            FestivalSyncResult result =
                    festivalSyncService.enrichFestivalDetailsByContentIds(List.of("694576"));

            // then
            assertThat(result.getTotalCount()).isEqualTo(1);
            assertThat(result.getCreatedCount()).isEqualTo(0);
            assertThat(result.getUpdatedCount()).isEqualTo(1);
            assertThat(result.getFailedCount()).isEqualTo(0);

            verify(festivalApiConverter, times(1)).hasDetailChanges(festival, detailItem);
            verify(festivalApiConverter, times(1)).updateDetailFields(festival, detailItem);
        }

        @Test
        @DisplayName("상세 API 성공 응답이지만 상세 정보가 변경되지 않은 경우 보강하지 않는다")
        void enrichFestivalDetailsByContentIds_no_change_test() throws Exception {
            // given
            Festival festival = Festival.builder()
                    .contentId("694576")
                    .title("가야문화축제")
                    .overview("상세 설명 없음")
                    .contactNumber(null)
                    .firstImageUrl("image1.jpg")
                    .thumbnailUrl("image2.jpg")
                    .address("경상남도 김해시 대성동")
                    .homepageUrl(null)
                    .startDate(LocalDateTime.of(2026, 4, 30, 0, 0))
                    .endDate(LocalDateTime.of(2026, 5, 3, 23, 59, 59))
                    .mapX(128.87)
                    .mapY(35.23)
                    .lDongRegnCd("48")
                    .status(FestivalStatus.UPCOMING)
                    .build();

            FestivalApiItem detailItem = createApiItem("694576", "가야문화축제");
            FestivalApiResponse detailResponse = createResponse(List.of(detailItem));

            when(festivalRepository.findByContentId("694576")).thenReturn(Optional.of(festival));
            when(festivalApiClient.fetchFestivalDetail("694576")).thenReturn(detailResponse);
            when(festivalApiConverter.hasDetailChanges(festival, detailItem)).thenReturn(false);

            // when
            FestivalSyncResult result =
                    festivalSyncService.enrichFestivalDetailsByContentIds(List.of("694576"));

            // then
            assertThat(result.getTotalCount()).isEqualTo(1);
            assertThat(result.getCreatedCount()).isEqualTo(0);
            assertThat(result.getUpdatedCount()).isEqualTo(0);
            assertThat(result.getFailedCount()).isEqualTo(0);

            verify(festivalApiConverter, times(1)).hasDetailChanges(festival, detailItem);
            verify(festivalApiConverter, never()).updateDetailFields(any(), any());
        }

        @Test
        @DisplayName("상세 API 실패 응답이면 보강하지 않고 실패 건수만 증가시킨다")
        void enrichFestivalDetailsByContentIds_fail_response_test() throws Exception {
            // given
            Festival festival = Festival.builder()
                    .contentId("694576")
                    .title("가야문화축제")
                    .overview("상세 설명 없음")
                    .contactNumber(null)
                    .firstImageUrl("image1.jpg")
                    .thumbnailUrl("image2.jpg")
                    .address("경상남도 김해시 대성동")
                    .homepageUrl(null)
                    .startDate(LocalDateTime.of(2026, 4, 30, 0, 0))
                    .endDate(LocalDateTime.of(2026, 5, 3, 23, 59, 59))
                    .mapX(128.87)
                    .mapY(35.23)
                    .lDongRegnCd("48")
                    .status(FestivalStatus.UPCOMING)
                    .build();

            FestivalApiResponse failResponse = createFailureResponse();

            when(festivalRepository.findByContentId("694576")).thenReturn(Optional.of(festival));
            when(festivalApiClient.fetchFestivalDetail("694576")).thenReturn(failResponse);

            // when
            FestivalSyncResult result =
                    festivalSyncService.enrichFestivalDetailsByContentIds(List.of("694576"));

            // then
            assertThat(result.getTotalCount()).isEqualTo(1);
            assertThat(result.getCreatedCount()).isEqualTo(0);
            assertThat(result.getUpdatedCount()).isEqualTo(0);
            assertThat(result.getFailedCount()).isEqualTo(1);

            verify(festivalApiConverter, never()).hasDetailChanges(any(), any());
            verify(festivalApiConverter, never()).updateDetailFields(any(), any());
        }

        @Test
        @DisplayName("존재하지 않는 contentId이면 실패 건수만 증가시킨다")
        void enrichFestivalDetailsByContentIds_not_found_test() {
            // given
            when(festivalRepository.findByContentId("999999")).thenReturn(Optional.empty());

            // when
            FestivalSyncResult result =
                    festivalSyncService.enrichFestivalDetailsByContentIds(List.of("999999"));

            // then
            assertThat(result.getTotalCount()).isEqualTo(1);
            assertThat(result.getCreatedCount()).isEqualTo(0);
            assertThat(result.getUpdatedCount()).isEqualTo(0);
            assertThat(result.getFailedCount()).isEqualTo(1);

            verify(festivalApiClient, never()).fetchFestivalDetail(anyString());
            verify(festivalApiConverter, never()).updateDetailFields(any(), any());
        }
    }

    private FestivalApiItem createApiItem(String contentId, String title) throws Exception {
        FestivalApiItem item = new FestivalApiItem();
        setField(item, "contentid", contentId);
        setField(item, "title", title);
        setField(item, "overview", "상세 설명 없음");
        setField(item, "tel", "055-330-6840");
        setField(item, "addr1", "경상남도 김해시");
        setField(item, "addr2", "대성동");
        setField(item, "homepage", "https://test.com");
        setField(item, "firstimage", "image1.jpg");
        setField(item, "firstimage2", "image2.jpg");
        setField(item, "mapx", "128.87");
        setField(item, "mapy", "35.23");
        setField(item, "lDongRegnCd", "48");
        setField(item, "eventstartdate", "20260430");
        setField(item, "eventenddate", "20260503");
        return item;
    }

    private FestivalApiResponse createResponse(List<FestivalApiItem> itemList) throws Exception {
        FestivalApiHeader header = new FestivalApiHeader();
        setField(header, "resultCode", "0000");
        setField(header, "resultMsg", "OK");

        FestivalApiItems items = new FestivalApiItems();
        setField(items, "item", itemList);

        FestivalApiBody body = new FestivalApiBody();
        setField(body, "items", items);
        setField(body, "numOfRows", itemList.size());
        setField(body, "pageNo", 1);
        setField(body, "totalCount", itemList.size());

        FestivalApiResponse.Response responseInner = new FestivalApiResponse.Response();
        setField(responseInner, "header", header);
        setField(responseInner, "body", body);

        FestivalApiResponse response = new FestivalApiResponse();
        setField(response, "response", responseInner);

        return response;
    }

    private FestivalApiResponse createFailureResponse() throws Exception {
        FestivalApiHeader header = new FestivalApiHeader();
        setField(header, "resultCode", "9999");
        setField(header, "resultMsg", "FAIL");

        FestivalApiBody body = new FestivalApiBody();

        FestivalApiResponse.Response responseInner = new FestivalApiResponse.Response();
        setField(responseInner, "header", header);
        setField(responseInner, "body", body);

        FestivalApiResponse response = new FestivalApiResponse();
        setField(response, "response", responseInner);

        return response;
    }

    private void setField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }
}