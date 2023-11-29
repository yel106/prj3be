package com.example.prj3be.service;

import com.example.prj3be.domain.Member;
import com.example.prj3be.repository.MemberRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Component
public class MemberDetailService implements UserDetailsService {
    private final MemberRepository memberRepository;

    public MemberDetailService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    // 로그인 시 DB에서 이용자 정보와 권한 정보를 가져옴
    // 해당 정보를 기반으로 userdetails.User 객체를 생성해서 리턴
    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return memberRepository.findOneWithAuthoritiesByLogId(username)
                .map(member->createUser(username, member))
                .orElseThrow(()-> new UsernameNotFoundException(username + "->데이터베이스에서 찾을 수 없습니다."));
    }

    private User createUser(String username, Member member) {
        if(member.getActivated() == null || !member.getActivated()){
            throw new RuntimeException(username+"이 활성화 되어 있지 않습니다.");
        }

        List<GrantedAuthority> grantedAuthorities = member.getAuthorities().stream()
                .map(authority -> new SimpleGrantedAuthority(authority.getAuthorityName()))
                .collect(Collectors.toList());

        System.out.println("grantedAuthorities = " + grantedAuthorities);

        return new User(member.getLogId(), member.getPassword(), grantedAuthorities);
    }
}
