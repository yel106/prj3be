package com.example.prj3be.service;

import com.example.prj3be.domain.Member;
import com.example.prj3be.domain.QMember;
import com.example.prj3be.dto.MemberEditFormDto;
import com.example.prj3be.repository.MemberRepository;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;


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
        member.setPassword(passwordEncoder.encode(dto.getPassword()));
        member.setAddress(dto.getAddress());
        member.setAge(dto.getAge());
        member.setGender(dto.getGender());
        member.setName(dto.getName());
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
        BooleanBuilder builder = new BooleanBuilder();

        if (category != null && keyword != null) {
            if ("all".equals(category)) {
                builder.and(member.name.containsIgnoreCase(keyword));
            } else if ("logId".equals(category)) {
                builder.and(member.logId.containsIgnoreCase(keyword));
            }
        }
        Predicate predicate = builder.hasValue() ? builder.getValue() : null;

        if (predicate != null) {
            return memberRepository.findAll(predicate, pageable);
        } else {
             return memberRepository.findAll(pageable);
        }
    }


    public Member findMemberByLogId(String logId) {
        Long id = memberRepository.findIdByLogId(logId);
        Optional<Member> findMember1 = memberRepository.findById(id);
        Member member = findMember1.get();

        return member;
    }

    public void deleteMember(Long id) {
        memberRepository.deleteById(id);
    }
}
