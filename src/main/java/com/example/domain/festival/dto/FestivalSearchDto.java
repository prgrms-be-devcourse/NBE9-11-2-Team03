package com.example.domain.festival.dto;

import com.example.domain.festival.entity.FestivalStatus;

public record FestivalSearchDto(
        String regionCode,
        FestivalStatus status,
        Integer month,
        String keyword,
        Double mapX, //내위치 경도
        Double mapY, //내위치 위도
        Double radiusKm //반경km
) {
}
