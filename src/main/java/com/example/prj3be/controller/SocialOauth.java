package com.example.prj3be.controller;

import com.example.prj3be.constant.SocialLoginType;
import com.example.prj3be.domain.SocialUser;
import com.example.prj3be.dto.SocialOauthToken;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.ResponseEntity;

public interface SocialOauth {
    String getOauthRedirectURL();
    ResponseEntity<String> requestAccessToken(String code);
    SocialOauthToken getAccessToken(ResponseEntity<String> response) throws JsonProcessingException;
    ResponseEntity<String> requestUserInfo(SocialOauthToken oauthToken);
    SocialUser getUserInfo(ResponseEntity<String> userInfoRes) throws JsonProcessingException;

    default SocialLoginType type() {
        if(this instanceof GoogleOauth) {
            return SocialLoginType.GOOGLE;
        } else if (this instanceof NaverOauth) {
            return SocialLoginType.NAVER;
        } else if (this instanceof KakaoOauth) {
            return SocialLoginType.KAKAO;
        } else {
            return null;
        }
    }
}
