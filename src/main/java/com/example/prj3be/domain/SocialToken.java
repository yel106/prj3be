package com.example.prj3be.domain;

import com.example.prj3be.constant.SocialLoginType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name="social_token")
@Getter @Setter
public class SocialToken extends BaseTimeEntity {
    @Id
    @Column(name="member_id")
    private Long id;
    @Enumerated(EnumType.STRING)
    private SocialLoginType socialLoginType;
    private String accessToken;
    private Integer expiresIn;
    private String refreshToken;
    private String tokenType;

    @ManyToOne
    @JoinColumn(name = "member_id", referencedColumnName = "member_id",  insertable=false, updatable=false)
    private Member member;
}
