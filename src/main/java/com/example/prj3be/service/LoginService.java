package com.example.prj3be.service;

import com.example.prj3be.domain.Member;
import com.example.prj3be.dto.SocialTokenDto;
import com.example.prj3be.jwt.LoginProvider;
import com.example.prj3be.jwt.TokenProvider;
import com.example.prj3be.repository.MemberRepository;
import com.nimbusds.jose.shaded.gson.JsonElement;
import com.nimbusds.jose.shaded.gson.JsonObject;
import com.nimbusds.jose.shaded.gson.JsonParser;
import jakarta.servlet.ServletRequest;
//import jdk.internal.jimage.BasicImageReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Set;

@Service
public class LoginService {

    @Value("${kakao.rest.api.key}")
    private String kakaoRestApiKey;

    @Value("${kakao.client.secret.key}")
    private String kakaoClientSecretKey;
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

        return accessToken;
    }

    public HashMap<String, Object> getUserInfo(String accessToken) {
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

}
