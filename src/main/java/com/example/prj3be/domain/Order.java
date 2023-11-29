package com.example.prj3be.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name="orders")
@NoArgsConstructor
@Getter @Setter
public class Order extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="order_id")
    private Long id;
    private Long price;
    private String itemName;

    private String orderUid; // 주문번호
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="member_id")
    private Member member;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id")
    private Payment payment;
    @Builder
    public Order(Long price, String itemName, String orderUid, Member member, Payment payment){
        this.price=price;
        this.itemName=itemName;
        this.orderUid=orderUid;
        this.member=member;
        this.payment=payment;
    }


}
