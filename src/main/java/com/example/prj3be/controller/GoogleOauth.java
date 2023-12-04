package com.example.prj3be.controller;

import com.example.prj3be.domain.SocialUser;
import com.example.prj3be.dto.SocialOauthToken;
import com.example.prj3be.exception.OAuthException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Component
@RequiredArgsConstructor
public class GoogleOauth implements SocialOauth {
    @Value("${social.google.url}")
    private String GOOGLE_SNS_BASE_URL;
    @Value("${social.google.client.id}")
    private String GOOGLE_SNS_CLIENT_ID;
    @Value("${social.google.callback.url}")
    private String GOOGLE_SNS_CALLBACK_URL;
    @Value("${social.google.client.secret}")
    private String GOOGLE_SNS_CLIENT_SECRET;
    @Value("${social.google.token.url}")
    private String GOOGLE_SNS_TOKEN_BASE_URL;

    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;

    @Override
    public String getOauthRedirectURL() {
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.set("scope", "https://www.googleapis.com/auth/userinfo.email" +
                "https://www.googleapis.com/auth/userinfo.profile");
        queryParams.set("response_type", "code");
        queryParams.set("client_id", GOOGLE_SNS_CLIENT_ID);
        queryParams.set("redirect_uri", GOOGLE_SNS_CALLBACK_URL);

        return UriComponentsBuilder
                .fromUriString(GOOGLE_SNS_BASE_URL)
                .queryParams(queryParams)
                .encode().build().toString();
    }

    @Override
    public ResponseEntity<String> requestAccessToken(String code) {
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.set("code", code);
        queryParams.set("client_id", GOOGLE_SNS_CLIENT_ID);
        queryParams.set("redirect_uri", GOOGLE_SNS_CALLBACK_URL);
        queryParams.set("client_secret", GOOGLE_SNS_CLIENT_SECRET);
        queryParams.set("grant_type", "authorization_code");

        ResponseEntity<String> responseEntity
                = restTemplate.postForEntity(GOOGLE_SNS_TOKEN_BASE_URL, queryParams, String.class);

        if(responseEntity.getStatusCode() == HttpStatus.OK) {
            return responseEntity;
        } else {
            throw new OAuthException("구글 로그인에 실패하였습니다");
        }
    }

    @Override
    public SocialOauthToken getAccessToken(ResponseEntity<String> response) throws JsonProcessingException {
        System.out.println("response.getBody() = " + response.getBody());
        return objectMapper.readValue(response.getBody(), SocialOauthToken.class);
    }

    @Override
    public ResponseEntity<String> requestUserInfo(SocialOauthToken oAuthToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + oAuthToken.getAccess_token()); //Bearer 뒤 공백 꼭 필요, 절대로 삭제하지 않기
        URI uri = UriComponentsBuilder
                .fromUriString("https://www.googleapis.com/oauth2/v1/userinfo")
                .build().toUri();

        return restTemplate.exchange(uri, HttpMethod.GET, new HttpEntity<>(headers), String.class);
    }

    @Override
    public SocialUser getUserInfo(ResponseEntity<String> userInfoRes) throws JsonProcessingException {
        return objectMapper.readValue(userInfoRes.getBody(), SocialUser.class);
    }
}
