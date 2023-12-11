package com.example.prj3be.controller.oauth;

import com.example.prj3be.constant.SocialLoginType;
import com.example.prj3be.dto.SocialUser;
import com.example.prj3be.dto.SocialOauthToken;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.ResponseEntity;

/* 모든 소셜 로그인은 아래의 과정을 거침
--------- 인가 코드 ---------
* 1. 클라이언트로부터 로그인 요청을 받음 (로그인 버튼 클릭)
* 2. 서버가 Auth 서버에 인가 코드를 요청함
* 3. 인가 코드를 받음
* 4. 인가 코드와 함께 소셜 로그인 요청, 사용자 인증 앱 설정 확인 (로그인 서비스 신청한 앱인지 아닌지)
* 5. 동의화면 출력
--------- 토큰 받기 ---------
* 6. 사용자가 앱에 제공할 개인 정보 지정
* 7. 302 Redirect URI로 인가 코드 전달
* 8. 서버에서 지정 방식에 맞춰 토큰 요청
* 9. 발급된 토큰 받기
--------- 정보 조회 ---------
* 10. 발급 받은 토큰으로 사용자 정보 조회
* 11. 서비스 회원 정보 확인 또는 가입 처리
* 12. 로그인 완료
*/
// 위 과정을 실행하는 메소드와 서비스의 공통점을 추출하여 인터페이스에 정의하였음
// 각 소셜 별 차이는 상속 후 다형성을 통해 충족함 (확장성이 좋기에 추후 다른 소셜 서비스 더할 수 있음)
public interface SocialOauth {
    String getOauthRedirectURL(); //2 - 인가 코드를 요청하는 URL을 생성하는 메소드(2)
    //8 - 토큰 요청 방식에 맞춰 URL 생성 후 요청 전송하는 메소드
    ResponseEntity<String> requestAccessToken(String code);
    //9 - 발급된 토큰 받기, 응답이 JSON인 경우가 있어서 변환 시 발생하는 Exception Method Signature에 넣음
    SocialOauthToken getAccessToken(ResponseEntity<String> response) throws JsonProcessingException;
    //10 - 발급 받은 토큰을 통해 사용자 정보 요청
    ResponseEntity<String> requestUserInfo(SocialOauthToken oauthToken);
    //10-1 - requestUserInfo를 통해 받아온 정보 저장
    SocialUser getUserInfo(ResponseEntity<String> userInfoRes) throws JsonProcessingException;
/*
    ResponseEntity<Object> updateToken(String socialType); 토큰 갱신 방식
    ResponseEntity<Object> logout(String socialType); //로그아웃
*/

    String getOauthRefreshURL(); //토큰 갱신 요청 URL을 생성하는 메소드

    // 인스턴스 타입을 확인하고 그에 따라 SocialLoginType의 Enum 인스턴스 반환
    default SocialLoginType type() {
        if(this instanceof GoogleOauth) {
            return SocialLoginType.GOOGLE;
        } else if (this instanceof NaverOauth) {
            return SocialLoginType.NAVER;
        } else if (this instanceof KakaoOauth) {
            return SocialLoginType.KAKAO;
        } else {
            return null;
        }
    }
}
