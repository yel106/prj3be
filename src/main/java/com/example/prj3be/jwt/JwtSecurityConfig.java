package com.example.prj3be.jwt;

import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

// TokenProvider, JwtFilter를 SecurityConfig에 적용할 때 사용할 클래스
public class JwtSecurityConfig extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {
    private TokenProvider tokenProvider;
    public JwtSecurityConfig(TokenProvider tokenProvider){
        this.tokenProvider = tokenProvider;
    }

    @Override
    public void configure(HttpSecurity http){
        //Jwt Filter를 통해 security 로직에 필터 등록
        http.addFilterBefore(
                new JwtFilter(tokenProvider),
                UsernamePasswordAuthenticationFilter.class
        );
    }
}
