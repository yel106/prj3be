package com.example.prj3be.service;

import com.example.prj3be.constant.SocialLoginType;
import com.example.prj3be.controller.SocialOauth;
import com.example.prj3be.domain.GetSocialOAuthRes;
import com.example.prj3be.domain.Member;
import com.example.prj3be.domain.SocialUser;
import com.example.prj3be.dto.SocialOauthToken;
import com.example.prj3be.exception.OAuthException;
import com.example.prj3be.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OauthService {
    private final List<SocialOauth> socialOauthList;
    private final MemberRepository memberRepository;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder encoder;

    // 1. redirectURL 만들기
    public String request(SocialLoginType socialLoginType) {
        SocialOauth socialOauth = this.findSocialOauthByType(socialLoginType);
        return socialOauth.getOauthRedirectURL();
    }

    // 2. 액세스 토큰 만들기
    public ResponseEntity<String> requestAccessToken(SocialLoginType socialLoginType, String code) {
        SocialOauth socialOauth = this.findSocialOauthByType(socialLoginType);
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

        String name = socialUser.getName();
        String email = socialUser.getEmail();

        //DB에 해당 유저가 없는지 조회 후 없으면 저장
        if(memberRepository.findByEmail(email) == null) {
            //TODO : social user
            Member member = new Member();
            member.setLogId(name);
            member.setEmail(email);
            member.setPassword(encoder.encode(email)); // 이메일을 비밀번호로 인코딩하여 저장 (null 방지)
            memberRepository.save(member);
        }

        //UserDetailsImpl userDetails = new UserDetailsImpl(user);
//        String jwtToken = jwtUtil.generateToken(userDetails);

        return new GetSocialOAuthRes(jwtToken, email, oAuthToken.getAccess_token(), oAuthToken.getToken_type());
    }
}
