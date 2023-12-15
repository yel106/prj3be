package com.example.prj3be.repository;

import com.example.prj3be.constant.SocialLoginType;
import com.example.prj3be.domain.SocialToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;

public interface SocialTokenRepository extends JpaRepository<SocialToken, String> {
    @Query("SELECT st FROM SocialToken st WHERE st.id = :id")
    Optional<SocialToken> findById(Long id);

    @Query("SELECT st.refreshToken FROM SocialToken st WHERE st.id = :id")
    String findRefreshTokenById(Long id);

    @Query("SELECT st.accessToken FROM SocialToken st WHERE st.id = :id")
    String findAccessTokenById(Long id);

    // 카카오: refresh Token의 남은 주기가 1개월 이상이면 새로 refresh 토큰 발급 안함
    @Modifying
    @Transactional
    @Query("UPDATE SocialToken st " +
            "SET st.accessToken = :#{#tokenInfoMap['accessToken']}, " +
            "    st.expiresIn = :#{#tokenInfoMap['expiresIn']}" +
            "    " +
            "    #if(:#{#tokenInfoMap['refreshToken']} != null)" +
            "        , st.refreshToken = :#{#tokenInfoMap['refreshToken']}" +
            "    #end " +
            "WHERE st.id = :id ")
    int updateTokenInfo(Long id, Map<String, Object> tokenInfoMap);

    @Transactional
    @Modifying
    @Query("DELETE FROM SocialToken st WHERE st.id = :id")
    int findAndDeleteTokenById(Long id);

    @Query("SELECT st.socialLoginType FROM SocialToken st WHERE st.id=:id")
    SocialLoginType findSocialLoginTypeById(Long id);

    @Query("SELECT st.expiresIn, st.updateTime FROM SocialToken st WHERE st.id=:id")
    Map<String, Object> getUpdateTimeAndExpiresInById(Long id);
}
