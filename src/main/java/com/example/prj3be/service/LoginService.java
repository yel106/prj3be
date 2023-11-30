package com.example.prj3be.service;

import com.example.prj3be.dto.SocialTokenDto;
import com.nimbusds.jose.shaded.gson.JsonElement;
import com.nimbusds.jose.shaded.gson.JsonObject;
import com.nimbusds.jose.shaded.gson.JsonParser;
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
import java.util.HashMap;
import java.util.Set;

@Service
public class LoginService {

    @Value("${kakao.rest.api.key}")
    private String kakaoRestApiKey;

    @Value("${kakao.client.secret.key}")
    private String kakaoClientSecretKey;

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
    }

    public String getKakaoAccessToken(String code) {
        String accessToken = "";
        String refreshToken = "";
        String requestURL = "https://kauth.kakao.com/oauth/token";

        try {
            URL url = new URL(requestURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            //setDoOutput() = OutputStream으로 Post 데이터 넘겨주는 옵션
            //Post 요청 수행 시 setDoOutput() 을 true로 설정
            conn.setDoOutput(true);

            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
            String sb = "grant_type=authorization_code" +
                    "&client_id=" + kakaoRestApiKey +
                    "&redirect_uri=http://localhost:3000/api/login/kakao" + //REDIRECT_URI
                    "&code=" + code;
            bufferedWriter.write(sb);
            bufferedWriter.flush();

            int responseCode = conn.getResponseCode();
            System.out.println("responseCode = " + responseCode);

            //요청을 통해 얻은 데이터를 InputStreamReader로 읽어오기
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = "";
            StringBuilder result = new StringBuilder();

            while((line = bufferedReader.readLine()) != null) {
                result.append(line);
            }
            System.out.println("Response Body = " + result);
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

    public void updateKakaoToken(int userId) throws Exception {
        SocialTokenDto kakaoToken = loginProvider.getKakaoToken(userId);
        String postURL = "http://kauth.kakao.com/oauth/token";
        SocialTokenDto newToken = null;

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
            JsonElement element = JsonParser.parseString(result.toString());
            Set<String> keySet = element.getAsJsonObject().keySet();

            //새로 발급 받은 accessToken 받아오기
            String accessToken = element.getAsJsonObject().get("access_token").getAsString();
            //refreshToken은 유효 기간이 1개월 미만일 경우에만 갱신되어 반환되므로 반환되지 않는 경우를 if문으로 처리
            if(keySet.contains("refresh_token")) {
                refreshToken = element.getAsJsonObject().get("refresh_token").getAsString();
            }

            if(refreshToken.equals("")) {
                newToken = new SocialTokenDto(accessToken, kakaoToken.getRefreshToken());
            } else {
                newToken = new SocialTokenDto(accessToken, kakaoToken.getRefreshToken());
            }

            bufferedReader.close();
            bufferedWriter.close();

            Authentication authentication = tokenProvider.getAuthentication(newToken.getAccessToken());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            int result = 0;
            if(newToken != null) {
                result = loginDao.updateKakaoToken(userId, newToken);
            }

            if(result == 0) {
                throw new Exception("Token update failed");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
