package com.example.domain.festival.service;

import com.example.domain.festival.entity.DetailSyncPendingReason;
import com.example.domain.festival.entity.FestivalDetailSyncPending;
import com.example.domain.festival.repository.FestivalDetailSyncPendingRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class FestivalDetailSyncPendingServiceTest {

    private final FestivalDetailSyncPendingRepository repository = mock(FestivalDetailSyncPendingRepository.class);

    private final FestivalDetailSyncPendingService service =
            new FestivalDetailSyncPendingService(repository);

    @Test
    @DisplayName("pending이 없으면 새로 생성한다")
    void saveOrUpdate_create_test() {
        when(repository.findByContentId("1001")).thenReturn(Optional.empty());

        service.saveOrUpdate("1001", DetailSyncPendingReason.EXCEPTION);

        verify(repository).save(any(FestivalDetailSyncPending.class));
    }

    @Test
    @DisplayName("pending이 이미 존재하면 retryCount 증가 및 갱신한다")
    void saveOrUpdate_update_test() {
        FestivalDetailSyncPending existing =
                FestivalDetailSyncPending.create("1001", DetailSyncPendingReason.EXCEPTION);

        when(repository.findByContentId("1001")).thenReturn(Optional.of(existing));

        service.saveOrUpdate("1001", DetailSyncPendingReason.SERVER_ERROR);

        // save는 호출되지 않음 (update 방식)
        verify(repository, never()).save(any());

        assertThat(existing.getRetryCount()).isEqualTo(2);
        assertThat(existing.getReason()).isEqualTo(DetailSyncPendingReason.SERVER_ERROR);
    }

    @Test
    @DisplayName("성공 시 pending을 제거한다")
    void remove_test() {
        service.remove("1001");

        verify(repository).deleteByContentId("1001");
    }

    @Test
    @DisplayName("pending 전체 조회 시 contentId 리스트로 반환한다")
    void findAllContentIds_test() {
        FestivalDetailSyncPending p1 =
                FestivalDetailSyncPending.create("1001", DetailSyncPendingReason.EXCEPTION);
        FestivalDetailSyncPending p2 =
                FestivalDetailSyncPending.create("1002", DetailSyncPendingReason.SERVER_ERROR);

        when(repository.findAllByOrderByLastFailedAtAsc())
                .thenReturn(java.util.List.of(p1, p2));

        var result = service.findAllContentIds();

        assertThat(result).containsExactly("1001", "1002");
    }
}