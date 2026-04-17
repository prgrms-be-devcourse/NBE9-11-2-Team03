package com.example.domain.bookmark.repository;

import com.example.domain.bookmark.entity.FestivalBookmark;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FestivalBookmarkRepository extends JpaRepository<FestivalBookmark,Long> {

    boolean existsByMemberIdAndFestivalId(Long memberId, Long festivalId);

    long countByMemberId(long memberId);

    Optional<FestivalBookmark> findByMemberIdAndFestivalId(Long memberId, Long festivalId);
}
