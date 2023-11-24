package com.example.prj3be.controller;

import com.example.prj3be.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("api/board")
public class BoardController {

    private final BoardService boardService;





}
