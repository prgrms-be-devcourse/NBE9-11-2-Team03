package com.example.domain.festival.dto;

import com.example.domain.festival.entity.FestivalStatus;

public record FestivalSearchDto(
        String area,
        FestivalStatus status,
        Integer month,
        String keyword
) {
}
