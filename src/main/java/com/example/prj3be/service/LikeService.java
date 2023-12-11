package com.example.prj3be.service;

import com.example.prj3be.domain.Likes;
import com.example.prj3be.domain.Member;
import com.example.prj3be.repository.LikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikeRepository likeRepository;

    public Map<String, Object> updateLike(Likes like, Member login) {
        if (login != null) {
            like.setMember(login);
        }

        boolean isLiked = likeRepository.existsByBoardIdAndMemberId(like.getBoard().getId(), like.getMember().getId());

        if (isLiked) {
            likeRepository.deleteByBoardIdAndMemberId(like.getBoard().getId(), like.getMember().getId());
        } else {
            likeRepository.save(like);
        }

        int countLike = likeRepository.countByBoardId(like.getBoard().getId());
        return Map.of("isLiked", isLiked, "countLike", countLike);
    }

    public boolean isLiked(Long boardId, Member login) {
        if (login != null) {
            return likeRepository.existsByBoardIdAndMemberId(boardId, login.getId());
        }
        return false;
    }

    public int getLikeCount(Long boardId) {
        return likeRepository.countByBoardId(boardId);
    }
}
