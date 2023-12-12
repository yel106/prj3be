package com.example.prj3be.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class OrderFormDto {
    private Integer amount;
    @NotBlank(message = "결제수단을 선택해주세요.")
    private String paymentKey;
    private String paymentUid;
}
