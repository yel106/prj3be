package com.example.prj3be.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

@Entity
@Table(name="cart")
@Data
@ToString
public class Cart {
    @Id
    @Column(name= "cartId")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

//    @OneToOne
//    @JoinColumn(name="memberId") //TODO: 멤버 테이블 작성 완료시 칼럼명 맞추기
//    private Member member;

    public static Cart createCart(Member member) {
        Cart cart = new Cart();
//        cart.setMember(member);
        return cart;
    }
}
