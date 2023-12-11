package com.example.prj3be.repository;

import com.example.prj3be.domain.Likes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
@Repository
public interface LikeRepository extends JpaRepository<Likes, Long> {

    boolean existsByBoardIdAndMemberId(Long boardId, Long id);

    void deleteByBoardIdAndMemberId(Long id, Long id1);

    int countByBoardId(Long id);

    @Repository
    public interface LikesRepository extends JpaRepository<Likes, Long> {

        int countByBoardId(Long boardId);

        void deleteByBoardIdAndMemberId(Long boardId, Long memberId);

        Likes findByBoardIdAndMemberId(Long boardId, Long memberId);
    }


}
