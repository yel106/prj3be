package com.example.prj3be.domain;

import com.example.prj3be.constant.SocialLoginType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name="social_token")
@Getter @Setter
public class SocialToken {
    @Id
    @Column(name="member_id") //JWT access Token에서 추출할 수 있는 정보로 지정 (fresh_token과 통일)
    private Long id;
    @Enumerated(EnumType.STRING)
    private SocialLoginType socialLoginType;
    private String accessToken;
    private Integer expiresIn;
    private String refreshToken;
    private String tokenType;
    @LastModifiedDate
    private LocalDateTime regTime;

    @ManyToOne
    @JoinColumn(name = "member_id", referencedColumnName = "member_id",  insertable=false, updatable=false)
    private Member member;
}
