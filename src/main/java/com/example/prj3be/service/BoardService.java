package com.example.prj3be.service;

import com.example.prj3be.domain.Board;
import com.example.prj3be.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class BoardService {
    private BoardRepository boardRepository;

    public void boardList(Board board) {
        boardRepository.findAll();
    }

    public void edit(Board board) {
        boardRepository.save(board);
    }

}
