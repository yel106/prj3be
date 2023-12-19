package com.example.prj3be.domain;

import com.example.prj3be.exception.OutOfStockException;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.ResponseEntity;

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
    private Long stockQuantity;

    public static CartItem createCartItem(Cart cart, Long id, String title, Double price, String fileUrl, int count, Long stockQuantity) {
        CartItem cartItem = new CartItem();
        cartItem.setId(id);
        cartItem.setCart(cart);
        cartItem.setTitle(title);
        cartItem.setPrice(price);
        cartItem.setCount(count);
        cartItem.setFileUrl(fileUrl);
        cartItem.setStockQuantity(stockQuantity);
        return cartItem;
    }

    public void addCount(int count) {
        if((this.count + 1) > stockQuantity) {
            this.count += 1;
        } else {
            throw new OutOfStockException("재고 수량 초과");
        }
    }

    public void subtractCount(int count) {
        this.count -= 1;
    }
}
