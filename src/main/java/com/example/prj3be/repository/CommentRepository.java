package com.example.prj3be.repository;

import com.example.prj3be.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface CommentRepository extends JpaRepository<Comment, Long>, QuerydslPredicateExecutor<Comment> {
    @Modifying
    @Query("DELETE FROM Comment c WHERE c.member.id = :memberId")
    int deleteCommentByMemberId(Long memberId);

    Long deleteCommentByBoardId(Long id);
}
