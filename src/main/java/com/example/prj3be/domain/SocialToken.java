package com.example.prj3be.domain;

import com.example.prj3be.constant.SocialLoginType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@EntityListeners(value= {AuditingEntityListener.class})
@Table(name="social_token")
@Getter @Setter
public class SocialToken {
    @Id
    @Column(name="log_id") //JWT access Token에서 추출할 수 있는 정보로 지정 (fresh_token과 통일)
    private String logId;
    private SocialLoginType socialLoginType;
    private String accessToken;
    private String refreshToken;
    private Integer expiresIn;
    private String tokenType;
    @LastModifiedDate
    private LocalDateTime regTime;
}
