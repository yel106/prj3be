package com.example.prj3be.service.oauth;

import com.example.prj3be.repository.SocialTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GoogleTokenManager implements SocialTokenManager {

    @Value("${social.google.client.id}")
    private String GOOGLE_SNS_CLIENT_ID;
    @Value("${social.google.client.secret}")
    private String GOOGLE_SNS_CLIENT_SECRET;
    @Value("${social.google.token.url}")
    private String GOOGLE_SNS_TOKEN_BASE_URL;

    private final SocialTokenRepository socialTokenRepository;

    @Override
    public boolean isTokenExpired(Long id) {
        return true;
    }; // 토큰 만료 여부 체크하는 논리형 메소드
    @Override
    public String getRefreshUri(Long id) {
        String refreshToken = socialTokenRepository.findRefreshTokenById(id);

        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.set("client_id", GOOGLE_SNS_CLIENT_ID);
        queryParams.set("client_secret", GOOGLE_SNS_CLIENT_SECRET);
        queryParams.set("refresh_token", refreshToken);
        queryParams.set("grant_type", "refresh_token");

        return UriComponentsBuilder
                .fromUriString(GOOGLE_SNS_TOKEN_BASE_URL)
                .queryParams(queryParams)
                .encode().build().toString();
    }; // 토큰 갱신 URI 생성하는 메소드

    @Override
    public ResponseEntity<String> checkAndRefreshToken(Long id) {
        return null;
    }; //토큰 갱신 요청하는 메소드

    // 요청해서 받은 토큰 정보를 토대로 테이블 갱신하는 메소드
    @Override
    public Map<String, Object> processRefreshResponse(ResponseEntity<String> response) {
        Map<String, Object> tokenInfoMap = new HashMap<>();
        return null;
    };

    @Override
    public void updateTokenInfo(Long id, Map<String, Object> tokenInfo) {

    };
}
