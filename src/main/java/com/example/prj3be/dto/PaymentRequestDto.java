package com.example.prj3be.dto;

import lombok.Getter;

@Getter
public class PaymentRequestDto {
    private String paymentKey;
    private String paymentUid;
    private Long amount;

}
