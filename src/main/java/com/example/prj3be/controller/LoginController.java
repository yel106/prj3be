package com.example.prj3be.controller;

import com.example.prj3be.dto.LoginDto;
import com.example.prj3be.dto.SocialOauthToken;
import com.example.prj3be.dto.TokenDto;
import com.example.prj3be.jwt.JwtFilter;
import com.example.prj3be.jwt.TokenProvider;
import com.example.prj3be.service.LoginService;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

import java.util.HashMap;

@RestController
@RequiredArgsConstructor
public class LoginController {
    private final TokenProvider tokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final LoginService loginService;
//    private final LoginProvider loginProvider;

    @Value("${button.image.url}")
    private String socialButtonImagePrefix;

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

    //로그인 버튼 이미지 불러오기
    @GetMapping("/api/login/image")
    public ResponseEntity<String> socialButtonImage() {
        return ResponseEntity.ok(socialButtonImagePrefix);
    }

}
