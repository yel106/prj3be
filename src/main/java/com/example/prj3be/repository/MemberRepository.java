package com.example.prj3be.repository;

import com.example.prj3be.domain.Member;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.Map;
import java.util.Optional;


public interface MemberRepository extends JpaRepository<Member,Long>,QuerydslPredicateExecutor<Member>{
    @Query("SELECT m.email FROM Member m WHERE m.email = :email")
    Optional<String> findEmailByEmail(String email);
    @Query("SELECT m.logId FROM Member m WHERE m.logId = :logId")
    Optional<String> findLogIdByLogId(String logId);
    @Query("SELECT m FROM Member m WHERE m.logId = :logId")
    Optional<Member> findByLogId(String logId);

    @Query("SELECT m FROM Member m WHERE m.email=:email AND m.logId = :logId")
    Member findByEmailAndLogId(String email, String logId);
    @Query("SELECT m FROM Member m WHERE m.email = :email")
    Member findByEmail(String email);

    @EntityGraph(attributePaths = "role")
    @Query("SELECT m FROM Member m WHERE m.logId = :logId")
    Optional<Member> findOneWithAuthoritiesByLogId(String logId);

    @Query("SELECT m.id FROM Member m WHERE m.logId = :logId")
    Long findIdByLogId(String logId);

    @Query("SELECT m.isSocialMember FROM Member m WHERE m.logId = :logId")
    Boolean checkSocialMemberByLogId(String logId);

//    @Query("SELECT new com.example.prj3be.dto.MemberInfoDto(m.logId, m.name, m.email, m.address, m.gender, m.role) FROM Member m WHERE m.logId = :logId")
//    MemberInfoDto findMemberInfoByLogId(String logId);
}
