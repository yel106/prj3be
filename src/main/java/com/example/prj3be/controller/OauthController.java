package com.example.prj3be.controller;

import com.example.prj3be.constant.SocialLoginType;
import com.example.prj3be.domain.SocialToken;
import com.example.prj3be.exception.OAuthException;
import com.example.prj3be.service.oauth.OauthService;
import lombok.RequiredArgsConstructor;
import org.hibernate.query.sqm.sql.ConversionException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class OauthController {
    private final OauthService oauthService;

    @GetMapping("/{socialLoginType}") //MemberLogin.js > handleSocialLogin
    public String socialLoginType(@PathVariable(name="socialLoginType") String socialLoginType) {
        SocialLoginType type = SocialLoginType.valueOf(socialLoginType);
//        System.out.println("socialLoginType = " + type);
        String redirectUrl = oauthService.request(type);
//        System.out.println("redirectUrl = " + redirectUrl);

        return redirectUrl;
    }

    @GetMapping("/{socialLoginType}/callback")
    public ResponseEntity<SocialToken> oAuthLogin(@PathVariable(name="socialLoginType") String socialLoginType,
                                                  @RequestParam(name="code") String code) throws IOException {
        SocialLoginType type = SocialLoginType.valueOf(socialLoginType.toUpperCase());
        SocialToken oAuthRes = oauthService.oAuthLogin(type, code);
        System.out.println("callback의 리턴 statement 이전");

        return ResponseEntity.ok(oAuthRes);
    }


    @ExceptionHandler(OAuthException.class)
    public ResponseEntity<String> oAuthExceptionHandleer(OAuthException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(ConversionException.class)
    public ResponseEntity<String> conversionExceptionHandler() {
        return new ResponseEntity<>("지원되지 않는 SocialLoginType입니다.", HttpStatus.NOT_FOUND);
    }
}
