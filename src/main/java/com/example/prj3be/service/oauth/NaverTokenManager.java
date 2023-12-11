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
public class NaverTokenManager implements SocialTokenManager {
    @Value("${social.naver.client.id}")
    private String NAVER_SNS_CLIENT_ID;
    @Value("${social.naver.client.secret}")
    private String NAVER_SNS_CLIENT_SECRET;
    @Value("${social.naver.token.uri}")
    private String NAVER_SNS_TOKEN_URI;

    private final SocialTokenRepository socialTokenRepository;

    @Override
    public boolean isTokenExpired(Long id) {
        return true;
    }; // 토큰 만료 여부 체크하는 논리형 메소드

    @Override
    public String getRefreshUri(Long id) {
        String refreshToken = socialTokenRepository.findRefreshTokenById(id);

        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.set("grant_type", "refresh_token");
        queryParams.set("client_id", NAVER_SNS_CLIENT_ID);
        queryParams.set("client_secret", NAVER_SNS_CLIENT_SECRET);
        queryParams.set("refresh_token", refreshToken);

        return UriComponentsBuilder
                .fromUriString(NAVER_SNS_TOKEN_URI)
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
