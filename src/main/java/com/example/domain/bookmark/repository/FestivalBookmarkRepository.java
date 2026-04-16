package com.example.domain.bookmark.repository;

import com.example.domain.bookmark.entity.FestivalBookmark;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FestivalBookmarkRepository extends JpaRepository<FestivalBookmark,Long> {
    long countByMemberId(long memberId);
}
