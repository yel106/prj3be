package com.example.prj3be.repository;

import com.example.prj3be.domain.SocialToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface SocialTokenRepository extends JpaRepository<SocialToken, String> {
    @Query("SELECT s FROM SocialToken s WHERE s.id = :id")
    Optional<SocialToken> findById(String id);
}
