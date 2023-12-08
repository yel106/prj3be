package com.example.prj3be.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

public class SecurityUtil {
    private static final Logger logger = LoggerFactory.getLogger(SecurityUtil.class);

    private SecurityUtil(){

    }

    // Security Context의 username을 리턴해줌
    public static Optional<String> getCurrentUserName(){
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(authentication == null){
            logger.debug("Security Context에 인증 정보가 없습니다.");
            return Optional.empty();
        }

        String username = null;
        //UserDetails 인터페이스를 구현한 객체인지 확인
        if(authentication.getPrincipal() instanceof UserDetails){
            UserDetails springSecurityUser = (UserDetails) authentication.getPrincipal();
            username = springSecurityUser.getUsername();
        } else if (authentication.getPrincipal() instanceof String) {
            username = (String) authentication.getPrincipal();
        }

        return Optional.ofNullable(username);
    }
}
