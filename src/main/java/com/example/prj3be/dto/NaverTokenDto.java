package com.example.prj3be.dto;

import lombok.Data;

@Data
public class NaverTokenDto {
    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private int expiresIn;
}
