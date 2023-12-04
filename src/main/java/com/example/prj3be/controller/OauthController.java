package com.example.prj3be.controller;

import com.example.prj3be.constant.SocialLoginType;
import com.example.prj3be.domain.GetSocialOAuthRes;
import com.example.prj3be.exception.OAuthException;
import com.example.prj3be.service.OauthService;
import jakarta.servlet.http.HttpServletResponse;
import org.hibernate.query.sqm.sql.ConversionException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("api/auth")
public class OauthController {
    private final OauthService oauthService;
    private final HttpServletResponse response;

    @GetMapping("/{socialLoginType}")
    public void socialLoginType(@PathVariable(name="socialLoginType") SocialLoginType socialLoginType) {
        String redirectUrl = oauthService.request(socialLoginType);
        try {
            response.sendRedirect(redirectUrl);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @GetMapping("/{socialLoginType}/callback")
    public GetSocialOAuthRes oAuthLogin(@PathVariable(name="socialLoginType") SocialLoginType socialLoginType,
                                        @RequestParam(name="code") String code) throws IOException {
        return oauthService.oAuthLogin(socialLoginType, code);
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
