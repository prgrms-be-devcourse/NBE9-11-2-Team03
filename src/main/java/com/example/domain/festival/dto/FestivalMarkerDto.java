package com.example.domain.festival.dto;

import com.example.domain.festival.entity.Festival;

public record FestivalMarkerDto(
        Long id,
        String title,
        Double mapX,
        Double mapY
) {
    public static FestivalMarkerDto from(Festival festival) {
        return new FestivalMarkerDto(
                festival.getId(),
                festival.getTitle(),
                festival.getMapX(),
                festival.getMapY()
        );
    }
}
