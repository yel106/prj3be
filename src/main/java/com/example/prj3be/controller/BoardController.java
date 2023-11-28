package com.example.prj3be.controller;

import com.example.prj3be.domain.Board;
import com.example.prj3be.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/board")
public class BoardController {
    private final BoardService boardService;

    @GetMapping ("list")
    public Page<Board> list(Pageable pageable,
                            @RequestParam (value = "c", defaultValue = "all") String category,
                            @RequestParam (value = "k", defaultValue = "") String keyword) {
        Page<Board> boardListPage = boardService.boardListAll(pageable, category, keyword);
        return boardListPage;
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