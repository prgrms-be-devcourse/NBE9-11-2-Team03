package com.example.domain.festival.controller;

import com.example.domain.festival.dto.FestivalResponseDto;
import com.example.domain.festival.dto.FestivalSearchDto;
import com.example.domain.festival.service.FestivalService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/festivals")
public class FestivalController {

    private final FestivalService festivalService;

    @GetMapping
    public ResponseEntity<?> searchFestivals(
            @ModelAttribute FestivalSearchDto searchDto,
            @PageableDefault(size = 10) Pageable pageable){

        Page<FestivalResponseDto> dtopage = festivalService.searchFestivals(searchDto, pageable).map(FestivalResponseDto::from);

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("content", dtopage.getContent());

        data.put("searchCondition", searchDto);

        List<String> sortList = pageable.getSort().stream()
                .map(order -> order.getProperty() + "," + order.getDirection())
                .toList();
        data.put("sort", sortList);

        //pagedto 추가하면 변경
        Map<String, Object> response = Map.of(
                "status", 200,
                "message", "축제 목록 조회 성공",
                "data", data,
                "page", dtopage.getNumber(),
                "size", dtopage.getSize(),
                "totalElements", dtopage.getTotalElements(),
                "totalPages", dtopage.getTotalPages()
        );

        return ResponseEntity.ok(response);

    }
}
