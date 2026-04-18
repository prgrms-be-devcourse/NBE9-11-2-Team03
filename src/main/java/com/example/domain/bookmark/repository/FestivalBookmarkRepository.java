package com.example.domain.bookmark.repository;

import com.example.domain.bookmark.entity.FestivalBookmark;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface FestivalBookmarkRepository extends JpaRepository<FestivalBookmark,Long> {

    boolean existsByMemberIdAndFestivalId(Long memberId, Long festivalId);

    long countByMemberId(long memberId);
    @Query("SELECT fb FROM FestivalBookmark fb JOIN FETCH fb.festival WHERE fb.member.id = :memberId")
    Page<FestivalBookmark> findByMemberId(Long memberId, Pageable pageable);

    Optional<FestivalBookmark> findByMemberIdAndFestivalId(Long memberId, Long festivalId);
}
