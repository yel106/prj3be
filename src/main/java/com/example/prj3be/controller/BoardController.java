package com.example.prj3be.controller;

import com.example.prj3be.domain.Board;
import com.example.prj3be.repository.BoardRepository;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/board")
public class BoardController {
    private final BoardRepository boardRepository;

    @GetMapping("/boards")
    public void board() {
        boardRepository.findAll();

    } 



}
