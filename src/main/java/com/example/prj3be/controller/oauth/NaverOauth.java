package com.example.prj3be.controller.oauth;

import com.example.prj3be.dto.SocialUser;
import com.example.prj3be.dto.SocialOauthToken;
import com.example.prj3be.exception.OAuthException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigInteger;
import java.net.URI;
import java.security.SecureRandom;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class NaverOauth implements SocialOauth {
    @Value("${social.naver.client.id}")
    private String NAVER_SNS_CLIENT_ID;
    @Value("${social.naver.client.secret}")
    private String NAVER_SNS_CLIENT_SECRET;
    @Value("${social.naver.redirect.uri}")
    private String NAVER_SNS_REDIRECT_URI;
    @Value("${social.naver.authorization.grantType}")
    private String NAVER_SNS_AUTH_TYPE;
    @Value("${social.naver.authorization.uri}")
    private String NAVER_SNS_AUTHORIZATION_URI;
    @Value("${social.naver.token.uri}")
    private String NAVER_SNS_TOKEN_URI;
    @Value("${social.naver.user.info.uri}")
    private String NAVER_SNS_USER_INFO_URI;

    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;

    @Override
    public String getOauthRedirectURL() {
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.set("response_type", "code");
        queryParams.set("client_id", NAVER_SNS_CLIENT_ID);
        queryParams.set("redirect_uri", NAVER_SNS_REDIRECT_URI);
        queryParams.set("state", new BigInteger(130, new SecureRandom()).toString());

        return UriComponentsBuilder
                .fromUriString(NAVER_SNS_AUTHORIZATION_URI)
                .queryParams(queryParams)
                .encode().build().toString();
    }

    @Override
    public String getOauthRefreshURL() {
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.set("grant_type", "refresh_token");
        queryParams.set("client_id", NAVER_SNS_CLIENT_ID);
        queryParams.set("client_secret", NAVER_SNS_CLIENT_SECRET);
        queryParams.set("refresh_token", "@@@@@@@@"); //TODO: fix
        return UriComponentsBuilder
                .fromUriString(NAVER_SNS_TOKEN_URI)
                .queryParams(queryParams)
                .encode().build().toString();
    }

    @Override
    public ResponseEntity<String> requestAccessToken(String code) {
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        try {
            queryParams.set("grant_type", NAVER_SNS_AUTH_TYPE);
            queryParams.set("client_id", NAVER_SNS_CLIENT_ID);
            queryParams.set("client_secret", NAVER_SNS_CLIENT_SECRET);
            queryParams.set("code", code);
            queryParams.set("state", new BigInteger(130, new SecureRandom()).toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        ResponseEntity<String> responseEntity =
                restTemplate.postForEntity(NAVER_SNS_TOKEN_URI, queryParams, String.class);

        if(responseEntity.getStatusCode() == HttpStatus.OK) {
            return responseEntity;
        } else {
            throw new OAuthException("네이버 로그인에 실패하였습니다.");
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
        headers.add("Authorization", "Bearer " + oAuthToken.getAccess_token());

        URI uri = UriComponentsBuilder
                .fromUriString(NAVER_SNS_USER_INFO_URI)
                .build().toUri();

        return restTemplate.exchange(uri, HttpMethod.GET, new HttpEntity<>(headers), String.class);
    }

    @Override
    public SocialUser getUserInfo(ResponseEntity<String> userInfoRes) throws JsonProcessingException {
        JSONObject jsonObject = (JSONObject) JSONValue.parse(Objects.requireNonNull(userInfoRes.getBody()));
        return objectMapper.readValue(jsonObject.get("response").toString(), SocialUser.class);
    }
}
