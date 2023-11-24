package com.example.prj3be.service;

import com.example.prj3be.domain.Member;
import com.example.prj3be.dto.MemberEditFormDto;
import com.example.prj3be.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {
    private final MemberRepository memberRepository;
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

    public List<Member> findMemberList() {
        List<Member> all = memberRepository.findAll();
        return all;
    }
}
