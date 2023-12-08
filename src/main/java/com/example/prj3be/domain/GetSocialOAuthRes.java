package com.example.prj3be.domain;

import com.example.prj3be.constant.SocialLoginType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@EntityListeners(value= {AuditingEntityListener.class})
@Table(name="social_tokens")
@Getter @Setter
public class GetSocialOAuthRes {
    @Id
    @Column(name="log_id")
    private String logId;
    private SocialLoginType socialLoginType;
    private String accessToken;
    private String refreshToken;
    private Integer expiresIn;
    private String tokenType;
    @LastModifiedDate
    private LocalDateTime regTime;
}
