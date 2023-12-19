package com.example.prj3be.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CartItemDto {
    private Long cartItemId;
    private String title;
    private Double price;
    private int count;
    private String fileUrl;

    public CartItemDto(Long cartItemId, String title, Double price, int count, String fileUrl) {
        this.cartItemId = cartItemId;
        this.title = title;
        this.price = price;
        this.count = count;
        this.fileUrl = fileUrl;
    }
}
