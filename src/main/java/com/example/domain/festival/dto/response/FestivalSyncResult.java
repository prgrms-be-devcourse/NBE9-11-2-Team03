package com.example.domain.festival.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

//동기화 결과 반환용 DTO
@Getter
@AllArgsConstructor
public class FestivalSyncResult {
    private int totalCount;
    private int createdCount;
    private int updatedCount;
}
