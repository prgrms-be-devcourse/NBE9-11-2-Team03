package com.example.domain.member.repository;

import com.example.domain.member.entity.Member;
import com.example.domain.member.entity.MemberStatus;
import com.example.domain.member.entity.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemberRepository extends JpaRepository<Member,Long> {
    Page<Member> findAllByReportCountGreaterThanEqualAndStatus(int reportCount, MemberStatus status,Pageable pageable );
}
