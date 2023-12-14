package com.example.prj3be.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @Builder
public class SocialOauthToken {
    private String access_token;
    private int expires_in;
    private String scope;
    private String token_type;
    private String id_token;
    private String refresh_token;
    private Integer refresh_token_expires_in;
}
