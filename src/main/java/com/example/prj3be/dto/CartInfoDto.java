package com.example.prj3be.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CartInfoDto {
    private Long cartItemId;
    private String itemName;
    private int price;
    private int count;
    private String fileUrl;
    public CartInfoDto(Long cartItemId, String itemName, int price, int count, String fileUrl) {
        this.cartItemId = cartItemId;
        this.itemName = itemName;
        this.price = price;
        this.count = count;
        this.fileUrl = fileUrl;
    }
}
