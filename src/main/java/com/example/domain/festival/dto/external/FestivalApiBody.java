package com.example.domain.festival.dto.external;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FestivalApiBody {
    private FestivalApiItems items;
    private int numOfRows;
    private int pageNo;
    private int totalCount;
}
