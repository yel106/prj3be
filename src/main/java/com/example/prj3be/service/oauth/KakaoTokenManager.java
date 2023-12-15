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


    @Override
    public boolean isTokenExpired(Long id) {
        System.out.println("KakaoTokenManager.isTokenExpired");
        LocalDateTime currentTime = LocalDateTime.now();
        System.out.println("currentTime = " + currentTime);
        Map<String, Object> tokenInfo = socialTokenRepository.getUpdateTimeAndExpiresInById(id);
        System.out.println("tokenInfo = " + tokenInfo);
        LocalDateTime updateTime = (LocalDateTime) tokenInfo.get("updateTime");
        System.out.println("updateTime = " + updateTime);
        int expiresIn = (Integer) tokenInfo.get("expiresIn");
        System.out.println("expiresIn = " + expiresIn);

        LocalDateTime expirationTime = updateTime.plusSeconds(expiresIn);
        System.out.println("expirationTime = " + expirationTime);
        System.out.println("토큰 시간 비교 중");

        return currentTime.isAfter(expirationTime);
    }; // 토큰 만료 여부 체크하는 논리형 메소드
    @Override
    public ResponseEntity<String> checkAndRefreshToken(Long id) {
        System.out.println("KakaoTokenManager.checkAndRefreshToken");
        if(!isTokenExpired(id)) {
            String accessToken = socialTokenRepository.findAccessTokenById(id);
            System.out.println("accessToken = " + accessToken);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.valueOf(MediaType.APPLICATION_FORM_URLENCODED_VALUE));
            headers.add("Authorization", "Bearer " + accessToken); //Bearer 뒤 공백 꼭 필요, 절대로 삭제하지 않기

            String refreshURI = getRefreshUri(id);
            System.out.println("refreshURI = " + refreshURI);
            System.out.println("성공적으로 URI 생성, 토큰 갱신 요청 보내고 리턴값 받는 중");

            return restTemplate.exchange(refreshURI, HttpMethod.POST, new HttpEntity<>(headers), String.class);
        }
        System.out.println(" 토큰 만료 오류 났음 - HttpStatus forbidden 리턴 중 ");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }; //토큰 갱신 요청하는 메소드
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
    }; // 토큰 갱신 URI 생성하는 메소드
    // 요청해서 받은 토큰 정보를 토대로 테이블 갱신하는 메소드
    @Override
    public Map<String, Object> processRefreshResponse(ResponseEntity<String> response) {
        System.out.println("KakaoTokenManager.processRefreshResponse");
        JSONObject jsonObject = (JSONObject) JSONValue.parse(Objects.requireNonNull(response.getBody()));
        Map<String, Object> tokenInfoMap = new HashMap<>();

        tokenInfoMap.put("accessToken", jsonObject.get("access_token"));
        tokenInfoMap.put("expiresIn", jsonObject.get("expires_in"));

        //토큰 갱신 요청 시 refreshToken이 만료되지 않았을 경우 access_token, expires_in만 돌려줌
        //따라서 nullPointException 발생 가능, 미리 응답에 있는지 체크하고 있으면 넣고 없으면 null 처리하여 수행
        if (jsonObject.containsKey("refresh_token")) {
            tokenInfoMap.put("refreshToken", jsonObject.get("refresh_token"));
        } else {
            tokenInfoMap.put("refreshToken", null);
        }

        return tokenInfoMap;
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
