package com.example.prj3be.service;

import com.example.prj3be.domain.Member;
import com.example.prj3be.domain.QMember;
import com.example.prj3be.dto.MemberEditFormDto;
import com.example.prj3be.repository.MemberRepository;
import com.example.prj3be.repository.MemberRepositoryImpl;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {
    private final MemberRepository memberRepository;
    private final MemberRepositoryImpl repository;


    public void signup(Member member) {
        memberRepository.save(member);
    }

    public Member findMemberById(Long id) {
        Optional<Member> findMember1 = memberRepository.findById(id);
        Member member = findMember1.get();
        return member;
    }

    public void update(Long id, MemberEditFormDto dto) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Member not found with id: " + id));
        member.setPassword(dto.getPassword());
        member.setEmail(dto.getEmail());
        member.setAddress(dto.getAddress());
    }

    public String getEmail(String email) {
        return memberRepository.findEmailByEmail(email)
                .orElse(null);
    }
    public String getLogId(String logId) {
        return memberRepository.findLogIdByLogId(logId)
                .orElse(null);
    }

    public Page<Member> findMemberList(Pageable pageable,String keyword,String category) {
        QMember member = QMember.member;
        Predicate predicate = createPredicate(keyword, category, member);

        return memberRepository.findAll(predicate, pageable);
    }

    private Predicate createPredicate(String keyword, String category, QMember member) {
        if ("all".equals(category)) {
            return member.name.containsIgnoreCase(keyword);
        } else if ("logId".equals(category)) {
            return member.logId.containsIgnoreCase(keyword);
        }
        return null;
    }
}
