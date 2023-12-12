package com.example.prj3be.service;

import com.example.prj3be.domain.Board;
import com.example.prj3be.domain.Likes;
import com.example.prj3be.domain.Member;
import com.example.prj3be.repository.BoardRepository;
import com.example.prj3be.repository.CartRepository;
import com.example.prj3be.repository.LikeRepository;
import com.example.prj3be.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

//@Service
//@RequiredArgsConstructor
//public class LikeService {
//
//    private final LikeRepository likeRepository;
//    private final BoardRepository boardRepository;
//
//    public Map<String, Object> updateLike(Likes like, Member login) {
//        if (login != null) {
//            like.setMember(login);
//        }
//
//        boolean isLiked = likeRepository.existsByBoardIdAndMemberId(like.getBoard().getId(), like.getMember().getId());
//
//        if (isLiked) {
//            likeRepository.deleteByBoardIdAndMemberId(like.getBoard().getId(), like.getMember().getId());
//        } else {
//            likeRepository.save(like);
//        }
//
//        int countLike = likeRepository.countByBoardId(like.getBoard().getId());
////        return Map.of("isLiked", isLiked, "countLike", countLike);
//        Board board = boardRepository.findById(like.getBoard().getId());
//        if (board != null){
//            board.setLikeCount(countLike);
//            boardRepository.save(board);
//        }
//        return Map.of("isLiked",isLiked,"countLike",countLike);
//    }
//
//    public boolean isLiked(Long boardId, Member login) {
//        if (login != null) {
//            return likeRepository.existsByBoardIdAndMemberId(boardId, login.getId());
//        }
//        return false;
//    }
//
//    public int getLikeCount(Long boardId) {
//        return likeRepository.countByBoardId(boardId);
//    }
//}
@Service
@RequiredArgsConstructor
@Transactional
public class LikeService {

    private final LikeRepository likeRepository;
    private final BoardRepository boardRepository;

    // TODO : 로그인 안했을 때 멤버정보 얻기위해 작성함 지워야 함
    private final MemberRepository memberRepository;

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

        // Board 엔티티를 가져와서 좋아요 수 업데이트
        Optional<Board> optionalBoard = boardRepository.findById(like.getBoard().getId());
        if (optionalBoard.isPresent()) {
            Board board = optionalBoard.get();
            List<Likes> likes = likeRepository.findByBoardId(board.getId());
            board.setLikes_board(likes);
            int updatedLikeCount = board.getLikes_board().size(); // 좋아요 수 업데이트
            boardRepository.save(board);
            countLike = updatedLikeCount;
        }

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

    public Map<String, Object> updateLike(Long id, Member login) {
        // TODO : 로그인 안했을 때 멤버정보 얻기위해 작성함 지워야함
        if (login == null) {
            login = memberRepository.findById(1L).get();
        }

        System.out.println("login = " + login);

        boolean isLiked = likeRepository.existsByBoardIdAndMemberId(id, login.getId());

        if (isLiked) {
            likeRepository.deleteByBoardIdAndMemberId(id, login.getId());
        } else {
            Likes like = new Likes();
            like.setBoard(boardRepository.findById(id).get());
            like.setMember(login);
            likeRepository.save(like);
        }

        return null;
    }
}
