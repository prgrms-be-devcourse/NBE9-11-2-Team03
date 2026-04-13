package com.example.domain.member.service;

import com.example.domain.member.dto.MemberPageResponse;
import com.example.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {
    private final MemberRepository memberRepository;

    public MemberPageResponse getAllMembers(Pageable pageable) {
        return MemberPageResponse.from(memberRepository.findAll(pageable));
    }
}
