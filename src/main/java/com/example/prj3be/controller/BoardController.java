package com.example.prj3be.controller;

import com.example.prj3be.domain.Board;
import com.example.prj3be.domain.Item;
import com.example.prj3be.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.example.prj3be.domain.QItem.item;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/board")
public class BoardController {
    private final BoardService boardService;

    @GetMapping("list")
    public List<Board> list(Board board) {
        return boardService.boardList(board);
    }

    @PostMapping("add")
    public void add(@Validated @RequestBody Board saveboard) {
        boardService.save(saveboard);
    }

    @GetMapping("id/{id}")
    public Optional<Board> get(@PathVariable Long id) {
        return boardService.getBoardById(id);
    }

    @PutMapping("/edit/{id}")
    public void update(@PathVariable Long id, @RequestBody Board updateBboard) {
        boardService.update(id, updateBboard);
    }

    @DeleteMapping("remove/{id}")
    public void delete(@PathVariable Long id) {
        boardService.delete(id);
    }


}