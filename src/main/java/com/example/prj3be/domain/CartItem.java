package com.example.prj3be.domain;
//
//import jakarta.persistence.*;
//import lombok.Data;
//import lombok.ToString;
//

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
@Table(name="cart_item")
public class CartItem {
    @Id
    @GeneratedValue
    @Column(name="cart_item_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name="cart_id") //한 사람당 한 카트
    private Cart cart;

    private String title;

    private String fileUrl;
    private Double price;

    private int count;

    public static CartItem createCartItem(Cart cart, Long id, String title, Double price, String fileURL, int count) {
        CartItem cartItem = new CartItem();
        cartItem.setId(id);
        cartItem.setCart(cart);
        cartItem.setTitle(title);
        cartItem.setPrice(price);
        cartItem.setCount(count);
        return cartItem;
    }

    public void addCount(int count) {
        this.count += count;
    }

    public void subtractCount(int count) {
        this.count -= count;
    }
}
