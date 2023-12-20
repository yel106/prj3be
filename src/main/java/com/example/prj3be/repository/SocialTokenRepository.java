package com.example.prj3be.repository;

import com.example.prj3be.constant.SocialLoginType;
import com.example.prj3be.domain.SocialToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

public interface SocialTokenRepository extends JpaRepository<SocialToken, String> {
    @Query("SELECT st FROM SocialToken st WHERE st.id = :id")
    Optional<SocialToken> findById(Long id);

    @Query("SELECT st.refreshToken FROM SocialToken st WHERE st.id = :id")
    String findRefreshTokenById(Long id);

    @Query("SELECT st.accessToken FROM SocialToken st WHERE st.id = :id")
    String findAccessTokenById(Long id);

    @Transactional
    @Modifying
    @Query("DELETE FROM SocialToken st WHERE st.id = :id")
    int findAndDeleteTokenById(Long id);

    @Query("SELECT st.socialLoginType FROM SocialToken st WHERE st.id=:id")
    SocialLoginType findSocialLoginTypeById(Long id);

    @Query("SELECT st.expiresIn, st.updateTime FROM SocialToken st WHERE st.id=:id")
    Map<String, Object> getUpdateTimeAndExpiresInById(Long id);

    @Query("SELECT st.expiresIn FROM SocialToken st WHERE st.id = :id")
    Integer getExpireTimeById(Long id);

    @Query("SELECT st.updateTime FROM SocialToken st WHERE st.id = :id")
    LocalDateTime getUpdateTimeById(Long id);

    @Query("SELECT st.refreshTokenExpiresIn FROM SocialToken st WHERE st.id = :id")
    Integer getRefreshTokenExpireTimeById(Long id);
}
