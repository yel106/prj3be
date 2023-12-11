package com.example.prj3be.service.oauth;

import org.springframework.http.ResponseEntity;

public interface SocialTokenManager {
    boolean isTokenExpired(String logId); // 토큰 만료 여부 체크하는 논리형 메소드
    void checkAndRefreshToken(String logId); //토큰 갱신 요청하는 메소드
    String getRefreshUri(String logId); // 토큰 갱신 URI 생성하는 메소드
    // 요청해서 받은 토큰 정보를 토대로 테이블 갱신하는 메소드
    void processRefreshResponse(String logId, ResponseEntity<String> response);

}
