package com.example.prj3be.service;

import com.example.prj3be.domain.Board;
import com.example.prj3be.domain.Comment;
import com.example.prj3be.domain.QComment;
import com.example.prj3be.repository.BoardRepository;
import com.example.prj3be.repository.CommentRepository;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final BoardRepository boardRepository;
    @Autowired
    private final JPAQueryFactory query;



    public Page<Comment> commentListAll(Pageable pageable, Long boardId) {
        List<Comment> content = query.selectFrom(QComment.comment)
                .where(QComment.comment.board.id.eq(boardId))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        JPAQuery<Long> countQuery = query.select(QComment.comment.count())
                .from(QComment.comment)
                .where(QComment.comment.board.id.eq(boardId));

        return PageableExecutionUtils.getPage(content,pageable,countQuery::fetchOne);
    }

    public Comment write(Comment saveComment) {
        Board board = new Board();
        Board saveBoard = boardRepository.save(board);

        Comment comment = Comment.builder()
                .board(saveBoard)
                .content(saveComment.getContent())
                .build();
        Comment addComment = commentRepository.save(comment);
        return addComment;
    }

    public Comment update(Long id, Comment updateComment) {
        Optional<Comment> contents = commentRepository.findById(id);
        if (contents.isPresent()) {
            Comment content = contents.get();
            content.setId(updateComment.getId());
            content.setContent(updateComment.getContent());
            return commentRepository.save(content);
        } else {
            throw new IllegalArgumentException("해당 리뷰를 찾을 수 없습니다.");
        }
    }

    public void delete(Long id) {
        commentRepository.deleteById(id);
    }

}
