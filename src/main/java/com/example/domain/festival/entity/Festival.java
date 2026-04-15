package com.example.domain.festival.entity;

import com.example.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FestivalStatus status = FestivalStatus.UPCOMING;

    @Column(nullable = false)
    private Integer viewCount = 0;

    @Column(nullable = false)
    private Integer likeCount = 0;

    @Column(nullable = false)
    private Double averageRate = 0.0;
}
