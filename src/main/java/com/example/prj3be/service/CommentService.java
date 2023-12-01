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

    public Comment write(Map<String, Object> map) {
        Comment comment = new Comment(null, map.get("content").toString(), new Board(Long.valueOf(map.get("boardId").toString()), null, null, null, null, null, null, null, null));
        return commentRepository.save(comment);
    }

    public Page<Comment> commentListAll(Pageable pageable) {
        Page<Comment> commentALl = commentRepository.findAll(pageable);
        return commentALl;

        //혹은
//        return commentRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("댓글이 존재하지 않습니다."));
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
