package com.example.prj3be.repository;

import com.example.prj3be.domain.FreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;


public interface FreshTokenRepository extends JpaRepository<FreshToken, String> {
    FreshToken findByLogId(String id);

    @Query("SELECT f.logId FROM FreshToken f WHERE f.token = :refreshToken")
    String findLogIdByToken(String refreshToken);
}
