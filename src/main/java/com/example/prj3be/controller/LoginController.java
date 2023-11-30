package com.example.prj3be.controller;

import com.example.prj3be.dto.LoginDto;
import com.example.prj3be.dto.TokenDto;
import com.example.prj3be.jwt.JwtFilter;
import com.example.prj3be.jwt.LoginProvider;
import com.example.prj3be.jwt.TokenProvider;
import com.example.prj3be.service.LoginService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.HashMap;

@RestController
@AllArgsConstructor
public class LoginController {
    private final TokenProvider tokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    @Value("${button.image.url}")
    private String socialButtonImagePrefix;
    private LoginService loginService;
    private LoginProvider loginProvider;

    public LoginController(TokenProvider tokenProvider, AuthenticationManagerBuilder authenticationManagerBuilder){
        this.tokenProvider = tokenProvider;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
    }

    @PostMapping("/login")
    public ResponseEntity<TokenDto> authorize(@Valid @RequestBody LoginDto loginDto){
        System.out.println("loginDto.getLogId() = " + loginDto.getLogId());
        System.out.println("loginDto.getPassword() = " + loginDto.getPassword());

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginDto.getLogId(), loginDto.getPassword());

        System.out.println("authenticationToken = " + authenticationToken);

        try {
            Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            System.out.println("authentication = " + authentication);

            String jwt = tokenProvider.createToken(authentication);

            System.out.println("jwt = " + jwt);

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add(JwtFilter.AUTHORIZATION_HEADER, "Bearer " + jwt);

            System.out.println("httpHeaders = " + httpHeaders);

            return new ResponseEntity<>(new TokenDto(jwt), httpHeaders, HttpStatus.OK);
        } catch (AuthenticationException e){
            System.out.println("인증 실패 :"+e.getMessage());
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping("/api/login/image")
    public ResponseEntity<String> socialButtonImage() {
        return ResponseEntity.ok(socialButtonImagePrefix);
    }

    @ResponseBody
    @GetMapping("/api/login/kakao")
    public ResponseEntity<String> kakaoLogin(@RequestParam(required = false) String code) {
        String postLoginRes = "";

        try{
            //URL에 포함된 code를 이용하여 액세스 토큰 발급
            String accessToken = loginService.getKakaoAccessToken(code);
            System.out.println("accessToken = " + accessToken);

            //액세스 토큰을 이용하여 카카오 서버에서 유저 정보(닉네임, 이메일) 받아오기
            HashMap<String, Object> userInfo = loginService.getUserInfo(accessToken);
            System.out.println("userInfo = " + userInfo);

//            PostLoginRes postLoginRes = null;

            //만약 DB에 해당 이메일을 가진 유저가 없다면 회원가입 시키고, 유저 식별자와 JWT 반환
            //전화번호, 성별, 및 기타 개인 정보는 사업자 번호가 없기 때문에 받아올 권한이 없어 테스트 불가능
            if(loginProvider.checkEmail(String.valueOf(userInfo.get("email"))) == 0) {
                return ResponseEntity.ok(null);
            } else {
                //해당 이메일을 가진 유저가 있다면 기존 유저의 로그인으로 판단하고 유저 식별자와 JWT 반환
                postLoginRes = loginProvider.getUserInfo(String.valueOf(userInfo.get("email")));
                return ResponseEntity.ok(postLoginRes);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Internal Server Error: " + e.getMessage());
        }
    }

    @ResponseBody
    @GetMapping("/api/login/kakao/{userId}")
    public ResponseEntity<String> updateKakaoToken(@PathVariable int userId) {
        String result = "";

        try {
            // JWT에서 id 추출

            int userIndexByJwt = Integer.parseInt(SecurityContextHolder.getContext().getAuthentication().getName());
            // userIndex와 접근한 유저가 같은지 확인
            if(userId != userIndexByJwt) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid User JWT");
            }
            loginService.updateKakaoToken(userId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

}