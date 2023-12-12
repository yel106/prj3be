package com.example.prj3be.service.oauth;

import com.example.prj3be.repository.SocialTokenRepository;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class KakaoTokenManager implements SocialTokenManager {
    @Value("${social.kakao.client.id}")
    private String KAKAO_SNS_CLIENT_ID;
    @Value("${social.kakao.token.uri}")
    private String KAKAO_SNS_TOKEN_URI;
    @Value("${social.kakao.revoke.uri}")
    private String KAKAO_SNS_REVOKE_URI;

    private final SocialTokenRepository socialTokenRepository;
    private final RestTemplate restTemplate;

    @Override
    public boolean isTokenExpired(Long id) {
        return true;
    }; // 토큰 만료 여부 체크하는 논리형 메소드
    @Override
    public ResponseEntity<String> checkAndRefreshToken(Long id) {
        String accessToken = socialTokenRepository.findAccessTokenById(id);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.valueOf(MediaType.APPLICATION_FORM_URLENCODED_VALUE));
        headers.add("Authorization", "Bearer " + accessToken); //Bearer 뒤 공백 꼭 필요, 절대로 삭제하지 않기

        String refreshURI = getRefreshUri(id);

        return restTemplate.exchange(refreshURI, HttpMethod.POST, new HttpEntity<>(headers), String.class);
    }; //토큰 갱신 요청하는 메소드
    @Override
    public String getRefreshUri(Long id) {
        String refreshToken = socialTokenRepository.findRefreshTokenById(id);

        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.set("grant_type", "refresh_token");
        queryParams.set("client_id", KAKAO_SNS_CLIENT_ID);
        queryParams.set("refresh_token", refreshToken);

        return UriComponentsBuilder
                .fromUriString(KAKAO_SNS_TOKEN_URI)
                .queryParams(queryParams)
                .encode().build().toString();
    }; // 토큰 갱신 URI 생성하는 메소드
    // 요청해서 받은 토큰 정보를 토대로 테이블 갱신하는 메소드
    @Override
    public Map<String, Object> processRefreshResponse(ResponseEntity<String> response) {
        JSONObject jsonObject = (JSONObject) JSONValue.parse(Objects.requireNonNull(response.getBody()));
        Map<String, Object> tokenInfoMap = new HashMap<>();

        tokenInfoMap.put("accessToken", jsonObject.get("access_token"));
        tokenInfoMap.put("tokenType", jsonObject.get("token_type"));
        tokenInfoMap.put("refreshToken", jsonObject.get("refresh_token"));
        tokenInfoMap.put("expiresIn", jsonObject.get("expires_in"));

        return tokenInfoMap;
    };

    @Override
    public void updateTokenInfo(Long id, Map<String, Object> tokenInfoMap) {
        socialTokenRepository.updateTokenInfo(id, tokenInfoMap);
    };

    @Override
    public ResponseEntity<String> revokeToken(Long id) {
        String accessToken = socialTokenRepository.findAccessTokenById(id);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.add("Authorization", "Bearer " + accessToken);

        String revokeTokenURI = UriComponentsBuilder.fromUriString(KAKAO_SNS_REVOKE_URI)
                .encode().build().toString();

        ResponseEntity<String> response = restTemplate.exchange(revokeTokenURI, HttpMethod.POST, new HttpEntity<>(headers), String.class);

        return response;
    }

}
