package com.example.prj3be.controller;

import com.example.prj3be.domain.SocialUser;
import com.example.prj3be.dto.SocialOauthToken;
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

import java.net.URI;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class KakaoOauth implements SocialOauth {
    @Value("${social.kakao.client.id}")
    private String KAKAO_SNS_CLIENT_ID;
    @Value("${social.kakao.redirect.uri}")
    private String KAKAO_SNS_CALLBACK_URL;
    @Value("${social.kakao.grantType}")
    private String KAKAO_SNS_AUTH_TYPE;
    @Value("${social.kakao.authorization.uri}")
    private String KAKAO_SNS_AUTHORIZATION_URI;
    @Value("${social.kakao.token.uri}")
    private String KAKAO_SNS_TOKEN_URI;
    @Value("${social.kakao.info.uri}")
    private String KAKAO_SNS_USER_INFO_URI;

    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;

    @Override
    public String getOauthRedirectURL() {
        MultiValueMap<String, String> queryParms = new LinkedMultiValueMap<>();
        queryParms.set("response_type", "code");
        queryParms.set("client_id", KAKAO_SNS_CLIENT_ID);
        queryParms.set("redirect_uri", KAKAO_SNS_CALLBACK_URL);

        return UriComponentsBuilder
                .fromUriString(KAKAO_SNS_AUTHORIZATION_URI)
                .queryParams(queryParms)
                .encode().build().toString();
    }

    @Override
    public ResponseEntity<String> requestAccessToken(String code) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.valueOf(MediaType.APPLICATION_FORM_URLENCODED_VALUE));
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.set("code", code);
        queryParams.set("client_id", KAKAO_SNS_CLIENT_ID);
        queryParams.set("redirect_uri", KAKAO_SNS_CALLBACK_URL);
        queryParams.set("grant_type", KAKAO_SNS_AUTH_TYPE);

        URI uri = UriComponentsBuilder
                .fromUriString(KAKAO_SNS_TOKEN_URI)
                .queryParams(queryParams)
                .encode().build().toUri();
        return restTemplate.exchange(uri, HttpMethod.GET, new HttpEntity<>(headers), String.class);
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
        headers.setContentType(MediaType.valueOf(MediaType.APPLICATION_FORM_URLENCODED_VALUE));

        URI uri = UriComponentsBuilder
                .fromUriString(KAKAO_SNS_USER_INFO_URI)
                .encode().build().toUri();

        return restTemplate.exchange(uri, HttpMethod.GET, new HttpEntity<>(headers), String.class);
    }

    @Override
    public SocialUser getUserInfo(ResponseEntity<String> userInfoRes) {

        JSONObject jsonObject = (JSONObject) JSONValue.parse(Objects.requireNonNull(userInfoRes.getBody()));
        JSONObject accountObject = (JSONObject) jsonObject.get("kakao_account");
        JSONObject profileObject = (JSONObject) accountObject.get("profile");

        return SocialUser.builder()
                .id((String) jsonObject.get("id"))
                .name((String) profileObject.get("nickname"))
                .email((String) accountObject.get("email"))
                .build();
    }
}
