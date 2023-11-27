package com.example.prj3be.dto;

import lombok.Data;

@Data
public class CartInfoDto {
    private Long cartItemId;
    private String itemName;
    private int price;
    private int count;
    private String imgUrl;
    public CartInfoDto(Long cartItemId, String itemName, int price, int count, String imgUrl) {
        this.cartItemId = cartItemId;
        this.itemName = itemName;
        this.price = price;
        this.count = count;
        this.imgUrl = imgUrl;
    }
}
