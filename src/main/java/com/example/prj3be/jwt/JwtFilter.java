package com.example.prj3be.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;
import java.util.List;

//jwt 커스텀 필터
public class JwtFilter extends GenericFilterBean {
    private static final Logger logger = LoggerFactory.getLogger(JwtFilter.class);
    public static final String AUTHORIZATION_HEADER = "Authorization";
    private TokenProvider tokenProvider;
    public JwtFilter(TokenProvider tokenProvider){
        this.tokenProvider = tokenProvider;
    }

    // Filter 인터페이스에서 상속된 메소드
    // jwt 토큰의 인증 정보를 현재 실행중인 SecurityContext에 저장하는 역할 수행
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws ServletException, IOException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        //실제 토큰을 가져옴
        String jwt = resolveToken(httpServletRequest);
        System.out.println("JwtFilter.doFilter");
        System.out.println("jwt = " + jwt);
        String requestURI = httpServletRequest.getRequestURI();
        System.out.println("requestURI = " + requestURI);
        //모니터링 경로는 빼고 검사
        if ("/actuator/prometheus".equals(requestURI)) {
            logger.info("'/actuator/prometheus' 경로에 대한 요청은 JWT 인증을 건너뜁니다.");
        }else{
        // 토큰이 정상적인 경우
        if(StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)){
            if(!requestURI.equals("/refreshToken") && !requestURI.equals("/api/logout")) {
                //토큰에서 authentication 가져와서
                Authentication authentication = tokenProvider.getAuthentication(jwt);
                //security context에 저장
                SecurityContextHolder.getContext().setAuthentication(authentication);
                logger.info("Security Context에 '{}' 인증 정보를 저장했습니다, uri: {}", authentication.getName(), requestURI);
            }else{// 리프레쉬 토큰인 경우
                logger.info("JWT token 리프레쉬 or 로그아웃");
            }
        } else{
            logger.info("유효한 JWT 토큰이 없습니다, uri: {}", requestURI);
        }}

        filterChain.doFilter(servletRequest, servletResponse);
    }

    // 필터링을 하기 위해서 토큰 정보를 꺼내오는 역할(요청 헤더에서 꺼내옴)
    private String resolveToken(HttpServletRequest httpServletRequest) {
        String bearerToken = httpServletRequest.getHeader(AUTHORIZATION_HEADER);

        if(StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")){
            return bearerToken.substring(7);
        }

        return null;
    }
}
