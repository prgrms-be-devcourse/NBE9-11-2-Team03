package com.example.domain.festival.repository;

import com.example.domain.festival.dto.FestivalSearchDto;
import com.example.domain.festival.entity.Festival;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FestivalRepository extends JpaRepository<Festival, Long>, FestivalRepositoryCustom {

    // 공공 API 동기화용
    Optional<Festival> findByContentId(String contentId);
    boolean existsByContentId(String contentId);

    // 축제 검색용
    Page<Festival> searchFestivals(FestivalSearchDto searchDto, Pageable pageable);
}
