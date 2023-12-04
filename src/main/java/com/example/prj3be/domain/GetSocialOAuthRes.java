package com.example.prj3be.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class GetSocialOAuthRes {
    private String jwtToken;
    private int user_num;
    private String accessToken;
    private String tokenType;
}
