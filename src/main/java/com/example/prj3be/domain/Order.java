package com.example.prj3be.domain;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name="order")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="order_id")
    private Long id;

//    @ManyToOne
//    @JoinColumn(name="member_id")
//    private Member member;

    @OneToMany(mappedBy = "order")
    private List<OrderItem> orderItems = new ArrayList<OrderItem>();

    private Date orderDate;

//    public void setMember(Member member) {
//        if(this.member != null){
//
//        }
//    }

}
