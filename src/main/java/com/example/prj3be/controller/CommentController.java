package com.example.prj3be.controller;

import com.example.prj3be.domain.Comment;
import com.example.prj3be.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/comment")
public class CommentController {
    private final CommentService commentService;

    @GetMapping("list")
    public Page<Comment> commentList(Pageable pageable) {
        Page<Comment> comments = commentService.commentListAll(pageable);
        return comments;
    }

    @PostMapping("add")
    public Comment save(@RequestBody Map<String, Object> map) { //요청된 본문을 받음
        Comment CommentWrite = commentService.write(map);
        return CommentWrite;
    }

    @PutMapping("update/{id}")
    public Comment update(@PathVariable Long id, @RequestBody Comment updateComment) {
        Comment editComment = commentService.update(id, updateComment);
        return editComment;
    }

    @DeleteMapping("delete/{id}")
    public void delete(@PathVariable Long id) {
        commentService.delete(id);
    }
}
