package com.example.prj3be.service;

import com.example.prj3be.domain.Board;
import com.example.prj3be.domain.Comment;
import com.example.prj3be.repository.BoardRepository;
import com.example.prj3be.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final BoardRepository boardRepository;

    public Comment write(Comment comment, Long id) {


        Long boardId = commentRepository.findBoardId(id);


        System.out.println("boardId = " + boardId);
//        System.out.println("id = " + ids); //null로 나옴
//        comment.setBoardId(ids); //comment의 board_id외래키에 board.Id를 대입하고 싶음.

        Comment comments = commentRepository.save(comment);
        System.out.println("comments = " + comments);
        return comments;


    }

    public Comment findCommentById(Long id) {
//        Optional<Comment> selectCommentById = commentRepository.findById(id);
//        Comment commentList = selectCommentById.get();//optional은 바로 return하지 못하고 .get() 메소드 이용해서 넣은 후에 반환
//        return commentList;

        //혹은
        return commentRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("댓글이 존재하지 않습니다."));
    }

    public Comment update(Long id, Comment comment) {
        Optional<Comment> comment1 = commentRepository.findById(id);
        Comment content = comment1.get();
        if (comment1.isPresent()) {
            content.setId(content.getId());
            content.setContent(content.getContent());
        }
        return content;
    }

    public void delete(Long id) {
        commentRepository.deleteById(id);
    }


}
