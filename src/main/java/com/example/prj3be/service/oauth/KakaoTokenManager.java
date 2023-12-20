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
public class KakaoTokenManager implements SocialTokenManager {
    @Value("${social.kakao.client.id}")
    private String KAKAO_SNS_CLIENT_ID;
    @Value("${social.kakao.token.uri}")
    private String KAKAO_SNS_TOKEN_URI;
    @Value("${social.kakao.logout.uri}")
    private String KAKAO_SNS_LOGOUT_URI;
    @Value("${social.kakao.revoke.uri}")
    private String KAKAO_SNS_REVOKE_URI;

    private final SocialTokenRepository socialTokenRepository;
    private final RestTemplate restTemplate;

    // 토큰 갱신 요청하는 메소드
    @Override
    public ResponseEntity<String> checkAndRefreshToken(Long id) {
        System.out.println("KakaoTokenManager.checkAndRefreshToken");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.valueOf(MediaType.APPLICATION_FORM_URLENCODED_VALUE));

        String refreshURI = getRefreshUri(id);
        System.out.println("refreshURI = " + refreshURI);
        System.out.println("성공적으로 URI 생성, 토큰 갱신 요청 보내고 리턴값 받는 중");

        return restTemplate.exchange(refreshURI, HttpMethod.POST, new HttpEntity<>(headers), String.class);
    };

    // 토큰 갱신 URI 생성하는 메소드
    @Override
    public String getRefreshUri(Long id) {
        System.out.println("KakaoTokenManager.getRefreshUri");
        String refreshToken = socialTokenRepository.findRefreshTokenById(id);
        System.out.println("refreshToken = " + refreshToken);

        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.set("grant_type", "refresh_token");
        queryParams.set("client_id", KAKAO_SNS_CLIENT_ID);
        queryParams.set("refresh_token", refreshToken);

        return UriComponentsBuilder
                .fromUriString(KAKAO_SNS_TOKEN_URI)
                .queryParams(queryParams)
                .encode().build().toString();
    };


    @Override
    public ResponseEntity socialLogout(Long id) {
        System.out.println("KakaoTokenManager.socialLogout");
        String accessToken = socialTokenRepository.findAccessTokenById(id);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.add("Authorization", "Bearer " + accessToken);

        String expireTokenURI = UriComponentsBuilder.fromUriString(KAKAO_SNS_LOGOUT_URI)
                .encode().build().toString();

        return tryRevokeToken(id, headers, expireTokenURI);
    }

    @Override
    public ResponseEntity revokeToken(Long id) {
        String accessToken = socialTokenRepository.findAccessTokenById(id);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.add("Authorization", "Bearer " + accessToken);

        String revokeTokenURI = UriComponentsBuilder.fromUriString(KAKAO_SNS_REVOKE_URI)
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
