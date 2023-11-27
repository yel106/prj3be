package com.example.prj3be.domain;

import com.example.prj3be.constant.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

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

    @Column(unique = true)
    private String email;

    private Integer age;

    private String gender; // 주민등록번호 뒤 첫번째 숫자.

    @Enumerated(EnumType.STRING)
    private Role role; //ADMIN,USER

    //order에 일대다 연관관계 매핑
    @OneToMany(mappedBy = "member")
    private List<Order> orders = new ArrayList<Order>();

    public Member() {
    }

}
