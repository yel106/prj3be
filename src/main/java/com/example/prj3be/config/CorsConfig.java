package com.example.prj3be.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

//Consider defining a bean of type 'org.springframework.web.filter.CorsFilter' in your configuration. 수행
//CorsFilter 빈을 직접 정의
@Configuration
public class CorsConfig {
    @Bean
    public CorsFilter corsFilter(){
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowCredentials(true);
        // 출처
        config.addAllowedOriginPattern("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");

        // 경로
        source.registerCorsConfiguration("/**", config);
        // => 모든 경로에 대해 모든 출처에서의 접근ㅎ 허용
        return new CorsFilter(source);
    }
}
