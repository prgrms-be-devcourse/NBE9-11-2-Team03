package com.example.domain.festival.repository;

import com.example.domain.festival.dto.FestivalSearchDto;
import com.example.domain.festival.entity.Festival;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FestivalRepositoryCustom {
    Page<Festival> searchFestivals(FestivalSearchDto searchDto, Pageable pageable);
}
