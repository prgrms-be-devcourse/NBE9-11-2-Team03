package com.example.domain.festival.controller;

import com.example.domain.festival.dto.*;
import com.example.domain.festival.entity.Festival;
import com.example.domain.festival.service.FestivalService;
import com.example.global.rsData.RsData;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/festivals")
public class FestivalController {

    private final FestivalService festivalService;

    //목록조회
    @GetMapping
    public ResponseEntity<RsData<FestivalPageResponseDto<FestivalListResponseDto>>> searchFestivals(
            @ParameterObject @ModelAttribute FestivalSearchRequestDto searchDto,
            @ParameterObject @PageableDefault(size = 10) Pageable pageable){

        Page<FestivalListResponseDto> dtopage = festivalService.searchFestivals(searchDto, pageable).map(FestivalListResponseDto::from);

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("content", dtopage.getContent());

        data.put("searchCondition", searchDto);

        List<String> sortList = pageable.getSort().stream()
                .map(order -> order.getProperty() + "," + order.getDirection())
                .toList();
        data.put("sort", sortList);

        Page<FestivalListResponseDto> responseDtoPage = festivalService.searchFestivals(searchDto, pageable)
                .map(FestivalListResponseDto::from);

        FestivalPageResponseDto<FestivalListResponseDto> pageData = FestivalPageResponseDto.from(dtopage);

        return ResponseEntity.ok(RsData.success("축제 목록 조회 성공", pageData));

    }

    //상세조회
    @GetMapping("/{id}")
    public ResponseEntity<RsData<FestivalDetailResponseDto>> getFestivalDetail(
            @PathVariable Long id
    ){
        Festival festival = festivalService.getFestival(id);
        FestivalDetailResponseDto responseDto = FestivalDetailResponseDto.from(festival);

        RsData<FestivalDetailResponseDto> rsData = new RsData<>("200", "축제 상세 조회 성공", responseDto);
        return ResponseEntity.ok(rsData);
    }

    @GetMapping("/nearby")
    public ResponseEntity<RsData<List<FestivalMarkerResponseDto>>> getNearbyFestivals(
            @ParameterObject @ModelAttribute FestivalSearchRequestDto searchDto
    ){
        FestivalSearchRequestDto mapSearchDto = searchDto.applyMapDefaults();
        List<Festival> festivals = festivalService.getNearbyMarkers(mapSearchDto);

        List<FestivalMarkerResponseDto> markerDtoList = festivals.stream()
                .map(FestivalMarkerResponseDto::from)
                .toList();

        RsData<List<FestivalMarkerResponseDto>> rsData = new RsData<>("200", "주변 축제 조회 성공", markerDtoList);
        return ResponseEntity.ok(rsData);
    }
}
