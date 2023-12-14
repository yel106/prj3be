package com.example.prj3be.service;

import com.example.prj3be.domain.Board;
import com.example.prj3be.domain.Comment;
import com.example.prj3be.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;

    public Page<Comment> commentListAll(Pageable pageable) {
        Page<Comment> commentALl = commentRepository.findAll(pageable);
        return commentALl;
    }

    public Comment write(Map<String, Object> map) {
        Comment comment = new Comment(null, map.get("content").toString(), new Board(Long.valueOf(map.get("boardId").toString()), null, null, null, null, null, null, null, null, null));
        return commentRepository.save(comment);
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
