package com.example.prj3be.service.oauth;

import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface SocialTokenManager {
    boolean isTokenExpired(Long id); // 토큰 만료 여부 체크하는 논리형 메소드
    ResponseEntity<String> checkAndRefreshToken(Long id); //토큰 갱신 요청하는 메소드
    String getRefreshUri(Long id); // 토큰 갱신 URI 생성하는 메소드
    // 요청해서 받은 토큰 정보를 변환하는 메소드
    Map<String, Object> processRefreshResponse(ResponseEntity<String> response);
    // 변환 끝난 토큰 정보를 테이블에 업데이트하는 메소드

    void updateTokenInfo(Long id, Map<String, Object> tokenInfoMap);

}
