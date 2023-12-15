package com.example.prj3be.service;

import com.example.prj3be.domain.Board;
import com.example.prj3be.domain.Comment;
import com.example.prj3be.domain.QComment;
import com.example.prj3be.dto.CommentFormDto;
import com.example.prj3be.repository.BoardRepository;
import com.example.prj3be.repository.CommentRepository;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
@Transactional
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final BoardRepository boardRepository;
    private final JPAQueryFactory query;


    public Page<Comment> commentListAll(Long boardId, Pageable pageable) {

//        SELECT c.content FROM comment c WHERE c.board_id = board_id;
//        QComment comment = QComment.comment;
//        BooleanBuilder builder = new BooleanBuilder();
//        if( boardId !=null ) {
//            builder.and(comment.board.id.eq(boardId));
//        }
//        Page<Comment> all = commentRepository.findAll(builder, pageable);
//        Stream<Comment> commentStream = all.get();
//        commentStream.forEach(System.out::println);
//        return all;
        List<Comment> content = query.selectFrom(QComment.comment)
                .where(QComment.comment.board.id.eq(boardId))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = query.select(QComment.comment.count())
                .from(QComment.comment)
                .where(QComment.comment.board.id.eq(boardId));

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    public void write(String boardId, CommentFormDto content) {
        System.out.println(content.getContent());
        Optional<Board> byId = boardRepository.findById(Long.valueOf(boardId));
        Board board1 = byId.orElseThrow();

        Comment comment = Comment.builder()
                .board(board1)
                .content(content.getContent())
                .build();
        commentRepository.save(comment);
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
