package com.example.prj3be.service;

import com.example.prj3be.constant.Role;
import com.example.prj3be.constant.SocialLoginType;
import com.example.prj3be.controller.oauth.SocialOauth;
import com.example.prj3be.domain.GetSocialOAuthRes;
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
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final PasswordEncoder encoder;
    private final TokenProvider tokenProvider;

    // 1. redirectURL 만들기
    public String request(SocialLoginType socialLoginType) {
        SocialOauth socialOauth = this.findSocialOauthByType(socialLoginType);
        System.out.println("socialOauth = " + socialOauth);
        return socialOauth.getOauthRedirectURL();
    }

    // 2. 액세스 토큰 만들기
    public ResponseEntity<String> requestAccessToken(SocialLoginType socialLoginType, String code) {
        SocialOauth socialOauth = this.findSocialOauthByType(socialLoginType);
        System.out.println("socialOauth = " + socialOauth);
        System.out.println("code = " + code);
        return socialOauth.requestAccessToken(code);
    }
    // 3. 소셜 타입 찾기
    private SocialOauth findSocialOauthByType(SocialLoginType socialLoginType) {
        return socialOauthList.stream()
                .filter(x -> x.type() == socialLoginType)
                .findFirst()
                .orElseThrow(() -> new OAuthException("알 수 없는 SocialLoginType 입니다."));
    }

    public GetSocialOAuthRes oAuthLogin(SocialLoginType socialLoginType, String code) throws IOException {
        SocialOauth socialOauth = findSocialOauthByType(socialLoginType);
        ResponseEntity<String> accessTokenResponse = socialOauth.requestAccessToken(code);
        SocialOauthToken oAuthToken = socialOauth.getAccessToken(accessTokenResponse);
        ResponseEntity<String> userInfoResponse = socialOauth.requestUserInfo(oAuthToken);
        SocialUser socialUser = socialOauth.getUserInfo(userInfoResponse);
        System.out.println("socialUser = " + socialUser);

        String name = socialUser.getName();
        String email = socialUser.getEmail();

        Member member = new Member();
        member.setLogId(name);
        member.setEmail(email);

        //DB에 해당 유저가 없는지 조회 후 없으면 저장
        if(memberRepository.findByEmail(email) == null) {
            // 이메일을 비밀번호로 인코딩하여 저장 (null 방지)
            member.setPassword(encoder.encode(email));
            // user로 role 지정
            member.setRole(Role.ROLE_USER);
            member.setActivated(true);
//            member.setSocialLogin(true); //소셜 로그인 계정이니 true
            // 회원 등록
            memberRepository.save(member);
        }

        System.out.println("member = " + member);

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(member.getLogId(), member.getEmail());

        System.out.println("authenticationToken = " + authenticationToken);

        try {
            Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            System.out.println("authentication = " + authentication);

            TokenDto tokens = tokenProvider.createTokens(authentication);

            System.out.println("jwt = " + tokens);

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add(JwtFilter.AUTHORIZATION_HEADER, "Bearer " + tokens);

            System.out.println("httpHeaders = " + httpHeaders);
            //httpHeader에 jwtToken 저장
            //client 쪽 로컬 스토리지에 소셜 토큰 관련 정보 저장하기 위해 필요한 정보들 dto로 묶어서 전송
            //소셜 타입(로그아웃/토큰 갱신 시 분류 위해), access_token, refresh_token, expires_in
            GetSocialOAuthRes oAuthRes = new GetSocialOAuthRes(tokens.getAccessToken(), member.getId(), oAuthToken.getAccess_token(), oAuthToken.getToken_type());
            System.out.println("oAuthRes = " + oAuthRes);
            return oAuthRes;
        } catch (AuthenticationException e){
            System.out.println("인증 실패 :"+e.getMessage());
            return null;
        }
    }
}
