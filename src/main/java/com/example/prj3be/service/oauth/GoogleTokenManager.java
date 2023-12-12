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
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

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
    private final RestTemplate restTemplate;

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
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        String refreshURI = getRefreshUri(id);

        return restTemplate.exchange(refreshURI, HttpMethod.POST, new HttpEntity<>(headers), String.class);
    }; //토큰 갱신 요청하는 메소드

    // 요청해서 받은 토큰 정보를 토대로 테이블 갱신하는 메소드
    @Override
    public Map<String, Object> processRefreshResponse(ResponseEntity<String> response) {
        JSONObject jsonObject = (JSONObject) JSONValue.parse(Objects.requireNonNull(response.getBody()));
        Map<String, Object> tokenInfoMap = new HashMap<>();

        tokenInfoMap.put("accessToken", jsonObject.get("access_token"));
        tokenInfoMap.put("expiresIn", jsonObject.get("expires_in"));
        tokenInfoMap.put("tokenType", jsonObject.get("token_type"));
        tokenInfoMap.put("refreshToken", jsonObject.get("refresh_token"));

        return tokenInfoMap;
    };

    @Override
    public void updateTokenInfo(Long id, Map<String, Object> tokenInfoMap) {
        socialTokenRepository.updateTokenInfo(id, tokenInfoMap);
    };

    @Override
    public ResponseEntity socialLogout(Long id) {
        //TODO: 여기도 로그아웃인지 아니면 탈퇴인지 확인
        return null;
    }

    @Override
    public ResponseEntity revokeToken(Long id) {
        String accessToken = socialTokenRepository.findAccessTokenById(id);
        HttpHeaders headers = new HttpHeaders();

        String revokeTokenURI = UriComponentsBuilder.fromUriString("https://accounts.google.com/o/oauth2/revoke")
                .queryParam("token", accessToken)
                .encode().build().toString();

        return tryRevokeToken(id, headers, revokeTokenURI);
    }

    @Override
    public ResponseEntity tryRevokeToken(Long id, HttpHeaders headers, String revokeTokenURI) {
        try {
            ResponseEntity response = restTemplate.exchange(revokeTokenURI, HttpMethod.POST, new HttpEntity<>(headers), String.class);
            if(response.getStatusCode() == HttpStatus.OK) {
                socialTokenRepository.findAndDeleteTokenById(id);
            }
            return response;
        } catch (HttpClientErrorException e) {
            e.printStackTrace();
            return ResponseEntity.status(e.getRawStatusCode()).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
