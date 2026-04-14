package com.example.domain.festival.entity;

import com.example.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Festival extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String contentId; // API 축제 고유 ID

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String overview;

    private String contactNumber;

    private String firstImageUrl;

    private String thumbnailUrl;

    @Column(nullable = false)
    private String address;

    private String homepageUrl;

    @Column(nullable = false)
    private LocalDateTime startDate;

    @Column(nullable = false)
    private LocalDateTime endDate;

    @Column(nullable = false)
    private Double mapX; // 경도

    @Column(nullable = false)
    private Double mapY; // 위도

    private String lDongRegnCd; // 법정동 코드

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FestivalStatus status = FestivalStatus.UPCOMING;

    @Builder.Default
    @Column(nullable = false)
    private Integer viewCount = 0;

    @Builder.Default
    @Column(nullable = false)
    private Integer bookMarkCount = 0;

    @Column(nullable = false)
    private Double averageRate = 0.0;


    //초기 데이터용 삭제
    public Festival(
            String contentId,
            String title,
            String overview,
            String address,
            LocalDateTime startDate,
            LocalDateTime endDate,
            Double mapX,
            Double mapY
    ) {
        this.contentId = contentId;
        this.title = title;
        this.overview = overview;
        this.address = address;
        this.startDate = startDate;
        this.endDate = endDate;
        this.mapX = mapX;
        this.mapY = mapY;

        // 기본값 세팅
        this.status = FestivalStatus.UPCOMING;
        this.viewCount = 0;
        this.bookMarkCount = 0;
        this.averageRate = 0.0;
    }
    public void updateAverageRating(Double averageRating) {
        this.averageRate = averageRating;
    }
}
