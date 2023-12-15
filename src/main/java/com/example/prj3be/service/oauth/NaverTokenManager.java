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

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

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
    private final RestTemplate restTemplate;

    @Override
    public boolean isTokenExpired(Long id) {
        System.out.println("NaverTokenManager.isTokenExpired");
        System.out.println("id = " + id);
        LocalDateTime currentTime = LocalDateTime.now();
        System.out.println("currentTime = " + currentTime);

//        Map<String, Object> tokenInfo = socialTokenRepository.getUpdateTimeAndExpiresInById(id); 안 먹힘 도대체 왜?????
        Integer expiresIn = socialTokenRepository.getExpireTimeById(id);
        System.out.println("expiresIn = " + expiresIn);
        LocalDateTime updateTime = socialTokenRepository.getUpdateTimeById(id);
        System.out.println("updateTimeTest = " + updateTime);

        LocalDateTime expirationTime = updateTime.plusSeconds(expiresIn);
        System.out.println("expirationTime = " + expirationTime);

        boolean isTokenValid = currentTime.isBefore(expirationTime);
        System.out.println("토큰이 유효한지: " + isTokenValid);

        return isTokenValid;
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
        if(isTokenExpired(id)) {
            String refreshURI = getRefreshUri(id);
            HttpHeaders headers = new HttpHeaders();
            return restTemplate.exchange(refreshURI, HttpMethod.POST, new HttpEntity<>(headers), String.class);
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }; //토큰 갱신 요청하는 메소드
    @Override
    public Map<String, Object> processRefreshResponse(ResponseEntity<String> response) {
        JSONObject jsonObject = (JSONObject) JSONValue.parse(Objects.requireNonNull(response.getBody()));
        Map<String, Object> tokenInfoMap = new HashMap<>();

        tokenInfoMap.put("accessToken", jsonObject.get("access_token"));
        tokenInfoMap.put("expiresIn", jsonObject.get("expires_in"));

        if (jsonObject.containsKey("refresh_token")) {
            tokenInfoMap.put("refreshToken", jsonObject.get("refresh_token"));
        } else {
            tokenInfoMap.put("refreshToken", null);
        }

        return tokenInfoMap;
    };


    @Override
    public ResponseEntity revokeToken(Long id) {
        String accessToken = socialTokenRepository.findAccessTokenById(id);

        HttpHeaders headers = new HttpHeaders();

        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.set("grant_type", "delete");
        queryParams.set("client_id", NAVER_SNS_CLIENT_ID);
        queryParams.set("client_secret", NAVER_SNS_CLIENT_SECRET);
        queryParams.set("access_token", accessToken);
        queryParams.set("service_provider", "NAVER");

        String revokeTokenURI = UriComponentsBuilder.fromUriString(NAVER_SNS_TOKEN_URI)
                .queryParams(queryParams)
                .encode().build().toString();

        return tryRevokeToken(id, headers, revokeTokenURI);
    }

    @Override
    public ResponseEntity socialLogout(Long id) {
        // 네이버 API는 로그아웃을 지원하지 않음
        // http://nid.naver.com/nidlogin.logout 로 이동하는 게 최선
        System.out.println("토큰 데이터베이스에서 삭제");
        socialTokenRepository.findAndDeleteTokenById(id);
        System.out.println("네이버 로그아웃 시도를 위해 HttpStatus.Found 리턴합니다. ");
        return ResponseEntity.status(HttpStatus.FOUND).build();
    }

    @Override
    public ResponseEntity tryRevokeToken(Long id, HttpHeaders headers, String revokeTokenURI) {
        try {
            ResponseEntity response = restTemplate.exchange(revokeTokenURI, HttpMethod.POST, new HttpEntity<>(headers), String.class);
            if (response.getStatusCode() == HttpStatus.OK) {
                socialTokenRepository.findAndDeleteTokenById(id);
            }
            return response;
        } catch (HttpClientErrorException e) {
            e.printStackTrace();
            return ResponseEntity.status(e.getRawStatusCode()).build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
