package com.example.domain.festival.dto;

import com.example.domain.festival.entity.Festival;
import com.example.domain.festival.entity.FestivalStatus;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record FestivalDetailResponseDto(
        Long id,
        String title,
        String firstImageUrl,
        LocalDate startDate,
        LocalDate endDate,
        String address,
        String contactNumber,
        String homepageUrl,
        String status,
        String overview,
        Double mapX,
        Double mapY,
        Integer viewCount,
        Integer bookMarkCount,
        Double averageRate,
        @JsonProperty("isBookmarked") boolean isBookmarked
)  {
    public static FestivalDetailResponseDto from(Festival festival, boolean isBookmarked) {
        FestivalStatus status = calculateStatus(
                festival.getStartDate(),
                festival.getEndDate()
        );

        return new FestivalDetailResponseDto(
                festival.getId(),
                festival.getTitle(),
                festival.getFirstImageUrl(),
                festival.getStartDate().toLocalDate(),
                festival.getEndDate().toLocalDate(),
                festival.getAddress(),
                festival.getContactNumber(),
                festival.getHomepageUrl(),
                status.name(),
                festival.getOverview(),
                festival.getMapX(),
                festival.getMapY(),
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