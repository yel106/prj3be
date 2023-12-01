package com.example.prj3be.controller;

import com.example.prj3be.domain.Board;
import com.example.prj3be.domain.Comment;
import com.example.prj3be.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/comment")
public class CommentController {
    private final CommentService commentService;

    @GetMapping("list")
    public Comment commentList(Long id) {
        Comment commentById = commentService.findCommentById(id);

        return commentById;
    }

    @PostMapping("add")
    public Comment save(@RequestBody Comment comment, Long id) { //요청된 본문을 받음
        Comment write = commentService.write(comment, id);
        return write;
    }










}
