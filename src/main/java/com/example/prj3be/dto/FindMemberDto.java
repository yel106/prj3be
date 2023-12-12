package com.example.prj3be.dto;

import com.example.prj3be.constant.Role;
import com.example.prj3be.domain.Member;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class FindMemberDto {
    private String logId;
    private String name;
    private String address;
    private String email;
    private String gender;
    private Role role;
    private Integer age;
    private LocalDateTime joinDate;

    public FindMemberDto() {
    }

    public FindMemberDto(Member member){
        this.logId = member.getLogId();
        this.name=member.getName();
        this.address=member.getAddress();
        this.email= member.getEmail();
        this.gender=member.getGender();
        this.role=member.getRole();
        this.age= member.getAge();
        this.joinDate=member.getRegTime();

    }
}
