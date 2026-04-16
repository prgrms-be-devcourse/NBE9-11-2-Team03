package com.example.domain.festival.service;

import com.example.domain.festival.dto.FestivalSearchDto;
import com.example.domain.festival.entity.Festival;
import com.example.domain.festival.repository.FestivalRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class FestivalService {

    private final FestivalRepository festivalRepository;

    public Page<Festival> searchFestivals(FestivalSearchDto searchDto, Pageable pageable) {
        return festivalRepository.searchFestivals(searchDto, pageable);
    }

    @Transactional
    public Festival getFestival(Long id) {
        Festival festival=  festivalRepository.findById(id)
                .orElseThrow(()-> new IllegalArgumentException("존재하지 않는 축제입니다."));

        festival.addViewCount();
        return festival;
    }

    public List<Festival> getNearbyMarkers(FestivalSearchDto searchDto){
        return festivalRepository.findNearbyFestivals(searchDto);
    }
}
