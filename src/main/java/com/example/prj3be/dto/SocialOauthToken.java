package com.example.prj3be.dto;

import lombok.Data;

@Data
public class SocialOauthToken {
    private String access_token;
    private int expires_in;
    private String scope;
    private String token_type;
    private String id_token;
    private String refresh_token;
}
