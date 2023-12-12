package com.example.prj3be.service.oauth;

import com.example.prj3be.constant.Role;
import com.example.prj3be.constant.SocialLoginType;
import com.example.prj3be.repository.SocialTokenRepository;
import com.example.prj3be.domain.SocialToken;
import com.example.prj3be.domain.Member;
import com.example.prj3be.dto.SocialUser;
import com.example.prj3be.dto.SocialOauthToken;
import com.example.prj3be.dto.TokenDto;
import com.example.prj3be.exception.OAuthException;
import com.example.prj3be.jwt.JwtFilter;
import com.example.prj3be.jwt.TokenProvider;
import com.example.prj3be.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OauthService {
    private final List<SocialOauth> socialOauthList;
    private final MemberRepository memberRepository;
    private final SocialTokenRepository socialTokenRepository;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final PasswordEncoder encoder;
    private final TokenProvider tokenProvider;

    // 1. redirectURL 만들기
    public String request(SocialLoginType socialLoginType) {
        SocialOauth socialOauth = this.findSocialOauthByType(socialLoginType);
//        System.out.println("socialOauth = " + socialOauth);
        return socialOauth.getOauthRedirectURL();
    }

    // 1-2. 소셜 타입 찾기
    private SocialOauth findSocialOauthByType(SocialLoginType socialLoginType) {
        return socialOauthList.stream()
                .filter(x -> x.type() == socialLoginType)
                .findFirst()
                .orElseThrow(() -> new OAuthException("알 수 없는 SocialLoginType 입니다."));
    }

    public void oAuthLogin(SocialLoginType socialLoginType, String code) throws IOException {
        SocialOauth socialOauth = findSocialOauthByType(socialLoginType);
        ResponseEntity<String> accessTokenResponse = socialOauth.requestAccessToken(code);
        SocialOauthToken oAuthToken = socialOauth.getAccessToken(accessTokenResponse);
        ResponseEntity<String> userInfoResponse = socialOauth.requestUserInfo(oAuthToken);
        SocialUser socialUser = socialOauth.getUserInfo(userInfoResponse);
        System.out.println("socialUser = " + socialUser);

        String name = socialUser.getName();
        String email = socialUser.getEmail();

        //DB에 해당 유저가 없는지 조회
        Member existingMember = memberRepository.findByEmailAndLogId(email, email);

        //정보 받을 객체 선언, 생성 + 초기화는 하지 않음
        Member member;
        SocialToken tokenInfo;

        if(existingMember == null) {
            member = new Member();
            member.setLogId(email);
            member.setName(name);
            member.setEmail(email);
            member.setPassword(encoder.encode(email));
            member.setRole(Role.ROLE_USER); // user로 role 지정
            member.setActivated(true);
            member.setIsSocialMember(true);
            memberRepository.save(member);
            System.out.println("New member created : " + member);

            //SocialToken에 소셜 타입(로그아웃/토큰 갱신 시 분류 위해), access_token, refresh_token, expires_in 저장
            tokenInfo = new SocialToken();
            tokenInfo.setId(member.getId());
            tokenInfo.setSocialLoginType(socialLoginType);
            tokenInfo.setAccessToken(oAuthToken.getAccess_token());
            tokenInfo.setRefreshToken(oAuthToken.getRefresh_token());
            tokenInfo.setExpiresIn(oAuthToken.getExpires_in());
            tokenInfo.setTokenType(oAuthToken.getToken_type());
            socialTokenRepository.save(tokenInfo);
            System.out.println("New token created : " + tokenInfo);
        } else {
            member = existingMember;
            System.out.println("Existing member : " + member);
        }

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(member.getLogId(), member.getEmail());
        System.out.println("authenticationToken = " + authenticationToken);

        try {
            Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            System.out.println("authentication = " + authentication);

            TokenDto tokens = tokenProvider.createTokens(authentication);

            System.out.println("jwt = " + tokens);

            //httpHeader에 jwtToken 저장
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add(JwtFilter.AUTHORIZATION_HEADER, "Bearer " + tokens);

            System.out.println("httpHeaders = " + httpHeaders);
        } catch (AuthenticationException e){
            System.out.println("인증 실패 :"+e.getMessage());
        }
    }
}
