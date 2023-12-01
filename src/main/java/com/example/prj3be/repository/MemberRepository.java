package com.example.prj3be.repository;

import com.example.prj3be.domain.Member;
import com.example.prj3be.dto.MemberInfoDto;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;


public interface MemberRepository extends JpaRepository<Member,Long>,QuerydslPredicateExecutor<Member>{
    @Query("SELECT m.email FROM Member m WHERE m.email = :email")
    Optional<String> findEmailByEmail(String email);
    @Query("SELECT m.logId FROM Member m WHERE m.logId = :logId")
    Optional<String> findLogIdByLogId(String logId);

    @Query("SELECT m FROM Member m WHERE m.email = :email")
    Member findByEmail(String email);

    @EntityGraph(attributePaths = "role")
    @Query("SELECT m FROM Member m WHERE m.logId = :logId")
    Optional<Member> findOneWithAuthoritiesByLogId(String logId);

    Optional<Member> findByLogId(String logId);

//    @Query("SELECT new com.example.prj3be.dto.MemberInfoDto(m.logId, m.name, m.email, m.address, m.gender, m.role) FROM Member m WHERE m.logId = :logId")
//    MemberInfoDto findMemberInfoByLogId(String logId);
}
