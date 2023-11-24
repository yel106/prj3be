package com.example.prj3be.repository;

import com.example.prj3be.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;


public interface MemberRepository extends JpaRepository<Member,Long> {
    @Query("SELECT m.email FROM Member m WHERE m.email = :email")
    Optional<String> findEmailByEmail(String email);
    @Query("SELECT m.logId FROM Member m WHERE m.logId = :logId")
    Optional<String> findLogIdByLogId(String logId);
}
