package com.example.prj3be.dto;

import com.example.prj3be.domain.Member;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2023-11-27T10:03:16+0900",
    comments = "version: 1.5.3.Final, compiler: javac, environment: Java 17.0.8 (Eclipse Adoptium)"
)
@Component
public class MemberMapperImpl implements MemberMapper {

    @Override
    public Member memberEditFormDtoToMember(MemberEditFormDto memberEditFormDto) {
        if ( memberEditFormDto == null ) {
            return null;
        }

        Member member = new Member();

        member.setPassword( memberEditFormDto.getPassword() );
        member.setAddress( memberEditFormDto.getAddress() );
        member.setEmail( memberEditFormDto.getEmail() );

        return member;
    }

    @Override
    public Member memberFormDtoToMember(MemberFormDto memberFormDto) {
        if ( memberFormDto == null ) {
            return null;
        }

        Member member = new Member();

        member.setLogId( memberFormDto.getLogId() );
        member.setPassword( memberFormDto.getPassword() );
        member.setName( memberFormDto.getName() );
        member.setAddress( memberFormDto.getAddress() );
        member.setEmail( memberFormDto.getEmail() );
        member.setRole( memberFormDto.getRole() );

        return member;
    }

    @Override
    public FindMemberDto memberToFindMemberDto(Member member) {
        if ( member == null ) {
            return null;
        }

        FindMemberDto findMemberDto = new FindMemberDto();

        findMemberDto.setLogId( member.getLogId() );
        findMemberDto.setName( member.getName() );
        findMemberDto.setAddress( member.getAddress() );
        findMemberDto.setEmail( member.getEmail() );
        findMemberDto.setGender( member.getGender() );
        findMemberDto.setRole( member.getRole() );
        findMemberDto.setAge( member.getAge() );

        return findMemberDto;
    }
}
