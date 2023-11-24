package com.example.prj3be.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

@Entity
@Data
@Table(name="cartItem")
public class CartItem {
    @Id
    @GeneratedValue
    @Column(name="itemId")
    private Long id;

    @ManyToOne
    @JoinColumn(name="cartId")
    private Cart cart;

//    @ManyToOne
//    @JoinColumn(name="itemId") //TODO: item 쪽 완성되면 이름 맞춰놓기
//    private Item item;

    private int count;

//    public static CartItem createCartItem(Cart cart, Item item, int count) {
//        CartItem cartItem = new CartItem();
//        cartItem.setCart(cart);
//        cartItem.setItem(item);
//        cartItem.setCount(count);
//        return cartItem;
//    }

    public void addCount(int count) {
        this.count += count;
    }
}
