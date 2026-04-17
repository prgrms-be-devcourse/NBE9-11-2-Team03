package com.example.domain.bookmark.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FestivalBookmarkResponseDto {

    private Long festivalId;
    private Long memberId;
    private boolean isBookmarked;
    private Integer bookmarkCount;


}
