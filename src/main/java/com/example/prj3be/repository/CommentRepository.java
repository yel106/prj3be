package com.example.prj3be.repository;

import com.example.prj3be.domain.Comment;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Query("SELECT b.id FROM Board b WHERE b.id = :id")
    Long findBoardId(@Param("id") Long id);

}
