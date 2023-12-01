package com.example.prj3be.service;

import com.example.prj3be.dto.NaverTokenDto;
import com.example.prj3be.dto.SocialTokenDto;
import com.example.prj3be.jwt.LoginProvider;
import com.example.prj3be.jwt.TokenProvider;
import com.example.prj3be.repository.MemberRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.shaded.gson.JsonElement;
import com.nimbusds.jose.shaded.gson.JsonObject;
import com.nimbusds.jose.shaded.gson.JsonParser;
//import jdk.internal.jimage.BasicImageReader;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.*;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Set;

@Service
public class LoginService {

    @Value("${kakao.rest.api.key}")
    private String kakaoRestApiKey;

    @Value("${kakao.client.secret.key}")
    private String kakaoClientSecretKey;

    @Value("${naver.client.id}")
    private String naverClientId;

    @Value("${naver.client.secret}")
    private String naverClientSecret;

    private LoginProvider loginProvider;
    private TokenProvider tokenProvider;

    @Autowired
    private MemberRepository memberRepository;

    // 버튼 클릭 시 로그인 창으로 이동
    public String createRedirectKakaoURL() {
        try {
            String redirectUri = "http://localhost:8080/api/login/kakao";
            String loginURL = "https://kauth.kakao.com/oauth/authorize" +
                    "?client_id=" + kakaoRestApiKey +
                    "&redirect_uri=" + redirectUri +
                    "&response_type=code&prompt=login";
            return loginURL;
        } catch (Exception e) {
            return null;
        }
    }

    //인가 코드 받는 url
    public String getKakaoAccessToken(String code) {
        String accessToken = "";
        String refreshToken = "";
        String requestURL = "https://kauth.kakao.com/oauth/token";
        String redirectURL = "http://localhost:8080/api/login/kakao";

        try {
            URL url = new URL(requestURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            //setDoOutput() = OutputStream으로 Post 데이터 넘겨주는 옵션
            //Post 요청 수행 시 setDoOutput() [기본값 false] 을 true로 설정
            conn.setDoOutput(true);

            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
            StringBuilder sb = new StringBuilder();
            sb.append("grant_type=authorization_code");
            sb.append("&client_id=" + kakaoRestApiKey);
            sb.append("&redirect_uri=" + redirectURL);
            sb.append("&code=" + code);
            bufferedWriter.write(sb.toString());
            bufferedWriter.flush();

            int responseCode = conn.getResponseCode();
            System.out.println("responseCode = " + responseCode);
            System.out.println(conn.getResponseMessage());

            //요청을 통해 얻은 데이터를 InputStreamReader로 읽어오기
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = "";
            StringBuilder result = new StringBuilder();

            while((line = bufferedReader.readLine()) != null) {
                result.append(line);
            }
            System.out.println("Response Body = " + result);

            // JSON 형식으로 오기 때문에 parsing 해야함
            JsonElement element = JsonParser.parseString(result.toString());

            accessToken = element.getAsJsonObject().get("access_token").getAsString();
            refreshToken = element.getAsJsonObject().get("refresh_token").getAsString();

            System.out.println("accessToken = " + accessToken);
            System.out.println("refreshToken = " + refreshToken);

            bufferedReader.close();
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // TODO : 갱신 토큰 어디다가 저장할지 논의 필요
        return accessToken;
    }

    public HashMap<String, Object> getKakaoUserInfo(String accessToken) {
        HashMap<String, Object> userInfo = new HashMap<>();
        String postURL = "https://kapi.kakao.com/v2/user/me";

        try {
            URL url = new URL(postURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");

            conn.setRequestProperty("Authorization", "Bearer " + accessToken);

            int responseCode = conn.getResponseCode();
            System.out.println("responseCode = " + responseCode);

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = "";
            StringBuilder result = new StringBuilder();

            while((line = br.readLine()) != null) {
                result.append(line);
            }
            System.out.println("response body = " + result);

            JsonElement element = JsonParser.parseString(result.toString());
            JsonObject properties = element.getAsJsonObject().get("properties").getAsJsonObject();
            JsonObject kakaoAccount = element.getAsJsonObject().get("kakao_account").getAsJsonObject();

            String nickname = properties.getAsJsonObject().get("nickname").getAsString();
            String email = kakaoAccount.getAsJsonObject().get("email").getAsString();

            userInfo.put("nickname", nickname);
            userInfo.put("email", email);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return userInfo;

        //해당 이메일을 가진 유저가 있다면 기존 유저의 로그인으로 판단하고 유저 식별자와 JWT 반환
    }


    public void updateKakaoToken(int userId) throws Exception {
        SocialTokenDto kakaoToken = getToken(userId);
        String postURL = "http://kauth.kakao.com/oauth/token";
        SocialTokenDto newToken = null;

        // 카카오 oauth에 토큰 갱신 요청 준비
        try {
            URL url = new URL(postURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            //POST 요청에 필요한 파라미터 OutputStream으로 전송
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
            String sb = "grant_type=refresh_token" +
                    "&client_id=" + kakaoRestApiKey +
                    "&refresh_token=" + kakaoToken.getRefreshToken() +
                    "&client_secret=" + kakaoClientSecretKey;
            bufferedWriter.write(sb);
            bufferedWriter.flush();

            //요청을 통해 얻은 데이터 InputStreamReader로 읽어오기
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = "";
            StringBuilder result = new StringBuilder();

            while((line = bufferedReader.readLine())!= null) {
                result.append(line);
            }

            System.out.println("response body = " + result);

            //받아온 결과 JSON으로 parsing 하여 저장하기
            JsonElement element = JsonParser.parseString(result.toString());
            Set<String> keySet = element.getAsJsonObject().keySet();

            //새로 발급 받은 accessToken 추출
            String accessToken = element.getAsJsonObject().get("access_token").getAsString();
            String refreshToken = "";

            //refreshToken은 유효 기간이 1개월 미만일 경우에만 갱신되어 반환되므로 반환되지 않는 경우를 if문으로 처리
            if(keySet.contains("refresh_token")) {
                refreshToken = element.getAsJsonObject().get("refresh_token").getAsString();
            }

            if(refreshToken.equals("")) {
                newToken = new SocialTokenDto(accessToken, kakaoToken.getRefreshToken());
            } else {
                newToken = new SocialTokenDto(accessToken, refreshToken);
            }

            bufferedReader.close();
            bufferedWriter.close();

            // Security context 업데이트 및 권한 토큰 부여
            Authentication authentication = tokenProvider.getAuthentication(newToken.getAccessToken());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean checkEmail(String email) {
        //DB에 해당 이메일을 가진 유저가 없는지 확인
        return memberRepository.findByEmail(email) == null;
    }

    public SocialTokenDto getToken(int userId) {

//        String token = servletRequest.getHeader("Authorization");
//        if(token !=null && token.startsWith("Bearer")) {
//            token = token.substring(7);
//        }
        return null;
        //TODO : getToken 함수 완성시키기
    }
    public String createRedirectNaverURL() {
        String baseURL = "https://nid.naver.com/oauth2.0/authorize";

        try {
            String redirectUri = "http://localhost:8080/api/login/naver";
            //naver 요청 시 state 코드를 요구하므로 (의미 없음, 자세한 사항은 LoginService:286~7) 랜덤값 생성
            SecureRandom random = new SecureRandom(); // 랜덤은 항상 바뀌기 때문에 (aka Math.Random) 하나 고정시키기
            String state = new BigInteger(130, random).toString(); //스트링으로 변환

            UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(baseURL)
                    .queryParam("response_type", "code")
                    .queryParam("client_id", naverClientId)
                    .queryParam("state", state) //임의 값
                    .queryParam("redirect_uri", redirectUri);
            System.out.println("builder = " + builder.toUriString());
            return builder.toUriString();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getNaverAccessToken(String code, String state, HttpServletResponse response) throws JsonProcessingException {
        RestTemplate tokenFetch = new RestTemplate();
        HttpHeaders naverTokenRequestHeader = new HttpHeaders();
        naverTokenRequestHeader.add("Content-type", "application/x-www-form-urlencoded");

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", naverClientId);
        params.add("client_secret", naverClientSecret);
        params.add("code", code);
        params.add("state", state);

        HttpEntity<MultiValueMap<String, String>> naverTokenRequest = new HttpEntity<>(params, naverTokenRequestHeader);

        ResponseEntity<String> oauthTokenResponse = tokenFetch.exchange(
                "https://nid.naver.com/oauth2.0/token",
                HttpMethod.POST,
                naverTokenRequest,
                String.class
        );

        System.out.println("oauthTokenResponse = " + oauthTokenResponse);

        ObjectMapper tokenToObject = new ObjectMapper();
        NaverTokenDto naverToken = null;
        try {
            naverToken = tokenToObject.readValue(oauthTokenResponse.getBody(), NaverTokenDto.class);
            return naverToken.getAccess_token(); //TODO : 다른 정보들은 어떻게 할지 상의
        } catch (JsonMappingException e) {
            e.printStackTrace();
            return null;
        }

        //        String accessToken = "";
//        String refreshToken = "";
//        String tokenType = "";
//        Integer expiresIn = 0;
//
//        String requestURL = "https://nid.naver.com/oauth2.0/token";
//        String redirectURL = "http://localhost:8080/api/login/naver";
//
//        try {
//            URL url = new URL(requestURL);
//            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//            conn.setRequestMethod("POST"); //GET도 가능
//            conn.setDoOutput(true);
//            //안 넣으면 기본이 application/json 이라서 grant_type is missing error 뜸
//            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
//
//            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
//            UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(requestURL)
//                    .queryParam("grant_type", "authorization_code")
//                    .queryParam("client_id", naverRestApiKey)
//                    .queryParam("client_secret", naverClientSecret)
//                    .queryParam("code", code)
//                    .queryParam("state", state);
//            System.out.println("builder = " + builder.toUriString());
//            /* 네이버는 사이트 간 요청 위조(cross-site request forgery) 공격을 방지하기 위해
//            애플리케이션에서 생성한 상태(state) 토큰값으로 URL 인코딩을 적용한 값을 사용, 발급 때 필수 */
//
//            bufferedWriter.write(builder.toUriString());
//            bufferedWriter.flush();
//
//            int responseCode = conn.getResponseCode();
//            System.out.println("responseCode = " + responseCode);
//            System.out.println("conn.getResponseMessage() = " + conn.getResponseMessage());
//
//            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//            String line = "";
//            StringBuilder result = new StringBuilder();
//
//            while((line = bufferedReader.readLine()) != null) {
//                result.append(line);
//            }
//
//            System.out.println("Response Body = " + result);
//
//            JsonElement element = JsonParser.parseString(result.toString());
//
//            if(element.isJsonObject()) {
//                JsonObject jsonObject = element.getAsJsonObject();
//                //에러가 났을 경우 에러 코드와 에러 메시지를 반환하므로, 그것들을 받아 출력하도록 함
//                if(jsonObject.has("error")) {
//                    String error = jsonObject.get("error").getAsString();
//                    String errorDesc = jsonObject.get("error_description").getAsString();
//                    System.out.println("error = " + error);
//                    System.out.println("errorDesc = " + errorDesc);
//                } else {
//                    //에러가 나지 않았을 경우 응답에서 아래 정보들을 추출
//                    // 접근 토큰
//                    accessToken = jsonObject.get("access_token").getAsString();
//                    // 갱신 토큰, 접근 토큰 망료 시 접근 토큰을 다시 발급 받을 때 사용
//                    refreshToken = jsonObject.get("refresh_token").getAsString();
//                    // 접근 토큰의 유효 기간(초 단위)
//                    expiresIn = jsonObject.get("expires_in").getAsInt();
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        // TODO: 갱신 토큰 어디다가 저장할지 논의 필요
//        return accessToken;
    }

    public HashMap<String, Object> getNaverUserInfo(String accessToken) {
        HashMap<String, Object> userInfo = new HashMap<>();
        String postURL = "https://openapi.naver.com/v1/nid/me";

        try {
            URL url = new URL(postURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST"); // 마찬가지로 GET, POST 둘 다 지원되지만 POST로 사용
            conn.setRequestProperty("Authorization", "Bearer" + accessToken);
            //Authorization: Bearer {접근 토큰} 형식으로 전달하기 위해 요청 해더 생성 (네이버 양식)

            int responseCode = conn.getResponseCode();
            System.out.println("responseCode = " + responseCode);

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = "";
            StringBuilder result = new StringBuilder();

            while ((line = br.readLine()) != null) {
                result.append(line);
            } //끝날 때까지 읽어서 concat

            // 확인용 출력
            System.out.println("response body = " + result);

            JsonElement element = JsonParser.parseString(result.toString());
            System.out.println("element = " + element);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
