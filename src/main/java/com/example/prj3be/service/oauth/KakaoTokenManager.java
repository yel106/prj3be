package com.example.prj3be.service.oauth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class KakaoTokenManager implements SocialTokenManager {
    @Value("${social.kakao.client.id}")
    private String KAKAO_SNS_CLIENT_ID;
    @Value("${social.kakao.token.uri}")
    private String KAKAO_SNS_TOKEN_URI;

    @Override
    public boolean isTokenExpired(String logId) {
        return true;
    }; // 토큰 만료 여부 체크하는 논리형 메소드
    @Override
    public void checkAndRefreshToken(String logId) {}; //토큰 갱신 요청하는 메소드
    @Override
    public String getRefreshUri(String logId) {
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.set("grant_type", "refresh_token");
        queryParams.set("client_id", KAKAO_SNS_CLIENT_ID);
        queryParams.set("refresh_token", "@@@@@@"); //TODO: fix

        return UriComponentsBuilder
                .fromUriString(KAKAO_SNS_TOKEN_URI)
                .queryParams(queryParams)
                .encode().build().toString();
    }; // 토큰 갱신 URI 생성하는 메소드
    // 요청해서 받은 토큰 정보를 토대로 테이블 갱신하는 메소드
    @Override
    public void processRefreshResponse(String logId, ResponseEntity<String> response) {};
}
