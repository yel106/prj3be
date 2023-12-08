package com.example.prj3be.jwt;

import com.example.prj3be.domain.FreshToken;
import com.example.prj3be.dto.TokenDto;
import com.example.prj3be.repository.FreshTokenRepository;
import com.example.prj3be.repository.MemberRepository;
import com.example.prj3be.service.MemberDetailService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.security.Key;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class TokenProvider implements InitializingBean {
    private final Logger logger = LoggerFactory.getLogger(TokenProvider.class);
    private static final String AUTHORITIES_KEY = "auth"; //사용자의 권한을 처리할 때 사용될 상수 정의
    private final String secret;
    private final long tokenExpiration;
    private Key key;
    @Autowired
    private FreshTokenRepository freshTokenRepository;

    private MemberRepository memberRepository;
    private MemberDetailService memberDetailService;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    //의존성 주입
    public TokenProvider(@Value("${jwt.token.key}")String secret,
                         @Value("${jwt.token.expiration}")long tokenExpiration, AuthenticationManagerBuilder authenticationManagerBuilder, MemberRepository memberRepository){
        this.secret = secret;
        this.tokenExpiration = tokenExpiration * 1000;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.memberRepository = memberRepository;
        this.memberDetailService = new MemberDetailService(memberRepository);
    }

    @Override
    public void afterPropertiesSet(){
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    @Transactional
    public TokenDto createTokens(Authentication authentication){
        String accessToken = createAccessToken(authentication);
        String refreshToken = createRefreshToken(authentication);
        String name = authentication.getName();

        System.out.println("TokenProvider.createTokens");
        System.out.println("name = " + name);

        // 기존에 refreshToken이 있다면
        if(freshTokenRepository.findByLogId(name) != null){
//            freshTokenRepository.deleteByLogId(name);//안되는 코드
            freshTokenRepository.deleteById(name);//정상작동 코드
        }
        // refreshToken을 DB에 저장
        FreshToken freshToken = new FreshToken();
        freshToken.setLogId(name);
        freshToken.setToken(refreshToken);
        freshTokenRepository.save(freshToken);

        return new TokenDto(accessToken, refreshToken);
    }

    @Transactional
    //authentication 객체에 포함되어 있는 권한 정보들을 통해 엑세스 토큰을 생성
    public String createAccessToken(Authentication authentication){
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        long now =(new Date()).getTime();
        Date validity = new Date(now+300*1000);//일단 만료 시간을 5분으로

        System.out.println("TokenProvider.createAccessToken");
        System.out.println("authorities = " + authorities);

        return Jwts.builder()
                .setSubject(authentication.getName())
                .claim(AUTHORITIES_KEY, authorities)
                .signWith(key, SignatureAlgorithm.HS512)
                .setExpiration(validity)
                .compact();
    }
    // 리프레시 토큰 생성
    public String createRefreshToken(Authentication authentication){
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        // 현재 시간과 토큰 만료 시간 설정, 엑세스 토큰의 24배(24시간)
        long now =(new Date()).getTime();
        System.out.println("now = " + now);
        Date validity = new Date(now+900*1000);//일단 만료시간을 15분으로

        System.out.println("TokenProvider.createRefreshToken");

        return Jwts.builder()
                .claim(AUTHORITIES_KEY, authorities)
                .setIssuedAt(new Date())
                .setExpiration(validity)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }


    //refresh 토큰을 이용해서 토큰들 재발급
    @Transactional
    public Authentication updateTokensByRefreshToken(String refreshToken){
        String logId = freshTokenRepository.findLogIdByToken(refreshToken);
        System.out.println("TokenProvider.updateTokensByRefreshToken's logId = " + logId);

        Authentication authentication = getAuthentication(refreshToken, logId);

        return authentication;
    }


    //엑세스 토큰의 정보를 이용해 Authentication 객체 리턴
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
        System.out.println("TokenProvider.getAuthentication");
        System.out.println("authorities = " + authorities);
        System.out.println("claims.getSubject() = " + claims.getSubject());

        // 권한 정보들로 유저 객체 만들기
        User principal = new User(claims.getSubject(), "", authorities);//사용자식별정보, 패스워드, 권한정보

        //유저객체, 토큰, 권한 객체로 Authentication 리턴
        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }

    // 리프레시 토큰의 정보를 이용해 Authentication 객체 리턴
    public Authentication getAuthentication(String token, String logId){
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
        User principal = new User(logId, "", authorities);//사용자식별정보, 패스워드, 권한정보

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
