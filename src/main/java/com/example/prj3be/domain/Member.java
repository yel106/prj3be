package com.example.prj3be.domain;

import com.example.prj3be.constant.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="member")
@Getter
@Setter
public class Member extends BaseTimeEntity{
    @Id
    @Column(name="member_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String logId; //로그인용 아이디
    private String password;
    private String name;
    private String address;
    private String email;

    private Integer age;

    private String gender; // 주민등록번호 뒤 첫번째 숫자.

    @Enumerated(EnumType.STRING)
    private Role role; //ADMIN,USER


    public Member() {
    }

}
