//package com.example.prj3be.domain;
//
//import jakarta.persistence.*;
//import lombok.Data;
//import lombok.ToString;
//
//@Entity
//@Data
//@Table(name="cart_item")
//public class CartItem {
//    @Id
//    @GeneratedValue
//    @Column(name="cart_item_id")
//    private Long id;
//
//    @ManyToOne
//    @JoinColumn(name="cart_id")
//    private Cart cart;
//
//    @ManyToOne
//    @JoinColumn(name="item_id")
//    private Item item;
//
//    private int count;
//
//    public static CartItem createCartItem(Cart cart, Item item, int count) {
//        CartItem cartItem = new CartItem();
//        cartItem.setCart(cart);
//        cartItem.setItem(item);
//        cartItem.setCount(count);
//        return cartItem;
//    }
//
//    public void addCount(int count) {
//        this.count += count;
//    }
//}
