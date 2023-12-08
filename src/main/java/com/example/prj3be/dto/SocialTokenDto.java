package com.example.prj3be.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SocialTokenDto {
    private String accessToken;
    private String refreshToken;
}
