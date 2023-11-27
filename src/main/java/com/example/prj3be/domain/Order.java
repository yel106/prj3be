package com.example.prj3be.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name="orders")
@Getter @Setter
public class Order extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="order_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="member_id")
    private Member member;

//    @OneToMany(mappedBy = "order")
//    private List<OrderItem> orderItems = new ArrayList<OrderItem>();

//    public void setMember(Member member) {
//        if(this.member != null){
//
//        }
//    }

}
