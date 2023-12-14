package com.example.prj3be.domain;

import com.example.prj3be.constant.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Entity
@Table(name="member")
@Getter
@Setter
//@ToString // TODO: 지워도됨 테스트하려구 작성함.. 안지워도됨.. 안지워도 별일없음
public class Member extends BaseTimeEntity{
    @Id
    @Column(name="member_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @Column(name="activated")
    private Boolean activated;

//    @ManyToMany
//    @JoinTable(
//            name="member_authority",
//            joinColumns = {@JoinColumn(name="member_id", referencedColumnName = "member_id")},
//            inverseJoinColumns = {@JoinColumn(name="authority_name", referencedColumnName = "authority_name")}
//    )
//    private Set<Authority> authorities;

    @OneToMany(mappedBy = "member")
    private List<Likes> likes_member;

    public Member() {

    }
}
