package com.example.prj3be.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class OrderFromDto {
    @NotBlank(message = "이름은 필수 입력값입니다.")
    private String name;
    @NotBlank(message = "주소는 필수 입력값입니다.")
    private String addr;
    @NotBlank(message = "전회번호는 필수 입력값입니다.")
    private String phone;
    private Integer totalPrice;
    @NotBlank(message = "결제수단을 선택해주세요.")
    private String payment;
}
