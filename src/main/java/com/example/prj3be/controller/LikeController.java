package com.example.prj3be.controller;

import com.example.prj3be.domain.Likes;
import com.example.prj3be.domain.Member;
import com.example.prj3be.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/like")
public class LikeController {

    private final LikeService service;

    @PostMapping(params = "boardId")
    public ResponseEntity<Map<String, Object>> like(@RequestParam(name="boardId") Long boardId) {
        String logId = SecurityContextHolder.getContext().getAuthentication().getName();

        System.out.println("id = " + boardId);

        return ResponseEntity.ok(service.updateLike(boardId, logId));
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>>like(Likes like,
                                                   @SessionAttribute(value= "login", required = false)Member login){
        System.out.println("LikeController.like");
        if (login == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(service.updateLike(like, login));
    }
    @GetMapping("board/{boardId}")
    public ResponseEntity<Map<String, Object>>get(
            @PathVariable Long boardId,
            @SessionAttribute(value = "login", required = false)Member login) {
        boolean isLiked = service.isLiked(boardId, login);
        int likeCount = service.getLikeCount(boardId);
        return ResponseEntity.ok(Map.of("like", isLiked, "countLike", likeCount));
    }
}
