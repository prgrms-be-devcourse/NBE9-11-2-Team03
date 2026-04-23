package com.example.domain.festival.dto;

import com.example.domain.festival.entity.Festival;
import com.example.domain.festival.entity.FestivalStatus;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record FestivalListResponseDto(
        Long id,
        String title,
        String thumbnail,
        LocalDate startDate,
        LocalDate endDate,
        String address,
        String status,
        Integer viewCount,
        Integer bookMarkCount,
        Double averageRate,
        @JsonProperty("isBookmarked") boolean isBookmarked
) {
    public static FestivalListResponseDto from(Festival festival, boolean isBookmarked) {
        FestivalStatus status = calculateStatus(
                festival.getStartDate(),
                festival.getEndDate()
        );

        return new FestivalListResponseDto(
                festival.getId(),
                festival.getTitle(),
                festival.getThumbnailUrl(),
                festival.getStartDate().toLocalDate(),
                festival.getEndDate().toLocalDate(),
                festival.getAddress(),
                status.name(),
                festival.getViewCount(),
                festival.getBookMarkCount(),
                festival.getAverageRate(),
                isBookmarked
        );
    }

    private static FestivalStatus calculateStatus(LocalDateTime startDate, LocalDateTime endDate) {
        LocalDateTime now = LocalDateTime.now();

        if (now.isBefore(startDate)) {
            return FestivalStatus.UPCOMING;
        }

        if (now.isAfter(endDate)) {
            return FestivalStatus.ENDED;
        }

        return FestivalStatus.ONGOING;
    }
}
