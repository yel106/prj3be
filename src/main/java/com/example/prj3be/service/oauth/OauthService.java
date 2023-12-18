package com.example.prj3be.service.oauth;

import com.example.prj3be.constant.Role;
import com.example.prj3be.constant.SocialLoginType;
import com.example.prj3be.domain.QSocialToken;
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
import com.querydsl.jpa.impl.JPAUpdateClause;
import jakarta.persistence.EntityManager;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@EnableScheduling
public class OauthService {
    private final List<SocialOauth> socialOauthList;
    private final List<SocialTokenManager> socialTokenManagers;
    private final MemberRepository memberRepository;
    private final SocialTokenRepository socialTokenRepository;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final PasswordEncoder encoder;
    private final TokenProvider tokenProvider;
    @Autowired
    private EntityManager entityManager;

    // 1. redirectURL 만들기
    public String loginRequest(SocialLoginType socialLoginType) {
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

    private SocialTokenManager findSocialTokenManagerByType(SocialLoginType socialLoginType) {
        return socialTokenManagers.stream()
                .filter(x -> x.type() == socialLoginType)
                .findFirst()
                .orElseThrow(() -> new OAuthException("알 수 없는 SocialLoginType 입니다."));
    }

    @Transactional
    public ResponseEntity<TokenDto> oAuthLogin(SocialLoginType socialLoginType, String code) throws IOException {
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
            // user로 role 지정
            member.setRole(Role.ROLE_USER);
            member.setActivated(true);
            member.setIsSocialMember(true);
            memberRepository.save(member);
            System.out.println("New member created : " + member);
        } else {
            member = existingMember;
            System.out.println("Existing member : " + member);
        }

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

            return new ResponseEntity<>(new TokenDto(tokens.getAccessToken(), tokens.getRefreshToken()), HttpStatus.OK);
        } catch (AuthenticationException e){
            System.out.println("인증 실패 :"+e.getMessage());
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    public ResponseEntity logoutRequest(Long id) {

        System.out.println("소셜 logoutRequest 연결 성공");
        SocialLoginType socialLoginType = socialTokenRepository.findSocialLoginTypeById(id);
        System.out.println("socialLoginType = " + socialLoginType);
        SocialTokenManager socialTokenManager = this.findSocialTokenManagerByType(socialLoginType);
        System.out.println("socialTokenManager = " + socialTokenManager);

        ResponseEntity response = socialTokenManager.socialLogout(id);
        System.out.println("소셜 logoutRequest 끝 : " + response);

        return response;
    }

    @Transactional
    public ResponseEntity<Integer> refreshAccessToken(String refreshToken) {
        System.out.println("OauthService.refreshAccessToken");
        //리프래쉬 토큰을 이용해 사용자의 아이디를 가져옴
        Long id = tokenProvider.getIdRefreshToken(refreshToken);
        System.out.println("id = " + id);

        try{
            //아이디로 소셜타입을 찾아와 저장
            //소셜 타입에 따른 메소드로 연결
            SocialLoginType socialLoginType = socialTokenRepository.findSocialLoginTypeById(id);
            SocialTokenManager socialTokenManager = this.findSocialTokenManagerByType(socialLoginType);
            System.out.println("토큰 유효한지 확인하고 요청 보내기");
            //토큰 유효한지 확인하고 요청 보내기
            ResponseEntity<String> response = socialTokenManager.checkAndRefreshToken(id);
            System.out.println("요청 보내고 받은 응답 " + response);
            System.out.println("받은 거 프로세스");
            //받은 거 프로세스해서
            Map<String, Object> tokenInfoMap = socialTokenManager.processRefreshResponse(response);
            System.out.println("tokenInfoMap = " + tokenInfoMap);
            System.out.println("테이블 업데이트");
            //테이블 업데이트하고
            updateTokenInfo(id, tokenInfoMap);
            //expires_in 리턴하기
            System.out.println("expires_in 리턴 중");
            Object expiresInObject = tokenInfoMap.get("expiresIn");
            System.out.println("expiresInObject = " + expiresInObject);
            System.out.println("expiresInObject.getClass() = " + expiresInObject.getClass());
            Integer expiresIn = Integer.parseInt(expiresInObject.toString());
            return ResponseEntity.ok().body(expiresIn);
        } catch (OAuthException e) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (HttpClientErrorException.Forbidden e) {
            System.out.println("소셜 토큰이 유효하지 않음");
            //소셜 토큰이 유효하지 않으면 Unauthorized 리턴됨
            System.out.println("e.getMessage() = " + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (RestClientException e) {
            System.out.println("뭔가 더 큰 에러 남");
            System.out.println("e.getMessage() = " + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @Transactional
    public void updateTokenInfo(Long id, Map<String, Object> tokenInfoMap) {
        System.out.println("OauthService.updateTokenInfo");
        //따라서 Querydsl with Spring Data JPA로 있을 때만 업데이트하고 없을 때는 업데이트 하지 않도록 함
        QSocialToken socialToken = QSocialToken.socialToken;

        JPAUpdateClause updateClause = new JPAUpdateClause(entityManager, socialToken);
        updateClause
                .set(socialToken.accessToken, tokenInfoMap.get("accessToken").toString())
                .set(socialToken.expiresIn, Integer.parseInt(tokenInfoMap.get("expiresIn").toString()));

        if (tokenInfoMap.get("refreshToken") != null) {
            updateClause
                    .set(socialToken.refreshToken, tokenInfoMap.get("refreshToken").toString());
        } else {
            //refreshToken이 null일 경우 기존 데이터 보존
            updateClause
                    .setNull(socialToken.refreshToken);
        }
        updateClause.where(socialToken.id.eq(id)).execute();
    };

    public ResponseEntity deleteSocial(Long id) {
        // (레코드 전체, socialTokenRepository -> findAndDeleteTokenById 사용)
        SocialLoginType socialLoginType = socialTokenRepository.findSocialLoginTypeById(id);
        SocialTokenManager socialTokenManager = this.findSocialTokenManagerByType(socialLoginType);
        ResponseEntity response = socialTokenManager.revokeToken(id);

        return response;
    }

}
