package com.example.domain.festival.repository;

import com.example.domain.festival.entity.Festival;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FestivalRepository extends JpaRepository<Festival,Long> {
}
