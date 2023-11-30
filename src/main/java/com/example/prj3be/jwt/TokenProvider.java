package com.example.prj3be.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Component
public class TokenProvider implements InitializingBean {
    private final Logger logger = LoggerFactory.getLogger(TokenProvider.class);
    private static final String AUTHORITIES_KEY = "auth"; //사용자의 권한을 처리할 때 사용될 상수 정의
    private final String secret;
    private final long tokenExpiration;
    private Key key;

    //의존성 주입
    public TokenProvider(@Value("${jwt.token.key}")String secret,
                         @Value("${jwt.token.expiration}")long tokenExpiration){
        this.secret = secret;
        this.tokenExpiration = tokenExpiration * 1000;
    }

    @Override
    public void afterPropertiesSet(){
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    //authentication 객체에 포함되어 있는 권한 정보들을 통해 토큰을 생성
    public String createToken(Authentication authentication){
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        long now =(new Date()).getTime();
        Date validity = new Date(now+this.tokenExpiration);

        return Jwts.builder()
                .setSubject(authentication.getName())
                .claim(AUTHORITIES_KEY, authorities)
                .signWith(key, SignatureAlgorithm.HS512)
                .setExpiration(validity)
                .compact();
    }

    //토큰의 정보를 이용해 Authentication 객체 리턴
    public Authentication getAuthentication(String token){
        // 토큰을 이용해 클레임 생성
        Claims claims = Jwts.parserBuilder() //jwt 파싱 빌더 생성
                            .setSigningKey(key) //jwt 검증 키 설정
                            .build()
                            .parseClaimsJws(token)
                            .getBody();

        // 클레임에서 권한 정보 빼내기 클레임에서 auth 키에 해당하는 값을 가져오고 배열 만들기, SimpleGrantedAutority 객체로 매핑하기
        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        // 권한 정보들로 유저 객체 만들기
        User principal = new User(claims.getSubject(), "", authorities);//사용자식별정보, 패스워드, 권한정보

        //유저객체, 토큰, 권한 객체로 Authentication 리턴
        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }
    // 토큰의 유효성 검증을 수행
    public boolean validateToken(String token){
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        }catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e){
            logger.info("잘못된 JWT 서명입니다.");
        }catch (ExpiredJwtException e){
            logger.info("만료된 JWT 토큰입니다.");
        }catch (UnsupportedJwtException e){
            logger.info("지원되지 않는 JWT 토큰입니다.");
        }catch (IllegalArgumentException e){
            logger.info("JWT 토큰이 잘못되었습니다.");
        }

        return false;
    }
}