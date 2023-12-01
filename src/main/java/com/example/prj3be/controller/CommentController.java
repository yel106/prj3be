package com.example.prj3be.controller;

import com.example.prj3be.domain.Comment;
import com.example.prj3be.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
        Comment write = commentService.write(map);
        return write;
    }










}
