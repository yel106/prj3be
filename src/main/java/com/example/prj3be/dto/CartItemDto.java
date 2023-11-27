package com.example.prj3be.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CartItemDto {

    @NotNull(message = "상품이 입력되지 않았습니다.")
    private Long itemId;

    @Min(value = 1, message="최소 1개 이상 담아주세요.")
    private int count;
}
