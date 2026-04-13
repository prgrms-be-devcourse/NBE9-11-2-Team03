package com.example.domain.festival.dto.external;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class FestivalApiItems {
    private List<FestivalApiItem> item;
}
