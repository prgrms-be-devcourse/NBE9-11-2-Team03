package com.example.domain.festival.dto;

import com.example.domain.festival.entity.Festival;

import java.time.LocalDate;

public record FestivalResponseDto(
        Long id,
        String title,
        String thumbnail,
        LocalDate startDate,
        LocalDate endDate,
        String address,
        String status,
        Integer viewCount,
        Integer likeCount,
        Double averageRate
) {
    public static FestivalResponseDto from(Festival festival) {
        return new FestivalResponseDto(
                festival.getId(),
                festival.getTitle(),
                festival.getThumbnailUrl(),
                festival.getStartDate().toLocalDate(),
                festival.getEndDate().toLocalDate(),
                festival.getAddress(),
                festival.getStatus().name(),
                festival.getViewCount(),
                festival.getBookMarkCount(),
                festival.getAverageRate()
        );
    }
}
