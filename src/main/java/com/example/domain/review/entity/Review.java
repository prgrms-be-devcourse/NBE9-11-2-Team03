package com.example.domain.review.entity;

import com.example.domain.festival.entity.Festival;
import com.example.domain.member.entity.Member;
import com.example.global.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Review extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "festival_id", nullable = false)
    private Festival festival;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    private String image;

    @Min(value = 1, message = "평점은 최소 1점입니다.")
    @Max(value = 5, message = "평점은 최대 5점입니다.")
    @Column(nullable = false)
    private Integer rating;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReviewStatus status = ReviewStatus.ACTIVE;

    @Column(nullable = false)
    private Integer likeCount = 0;

    @Column(nullable = false)
    private Integer reportCount = 0;

    public Review(Member member, Festival festival, String content, String image, Integer rating) {
        this.member = member;
        this.festival = festival;
        this.content = content;
        this.image = image;
        this.rating = rating;
        this.status = ReviewStatus.ACTIVE;
        this.likeCount = 0;
        this.reportCount = 0;
    }
    public void reviewBlind(){
        this.status=ReviewStatus.BLIND;
    }
    public  void reportCountReset(){
        this.reportCount=0;
    }
}

