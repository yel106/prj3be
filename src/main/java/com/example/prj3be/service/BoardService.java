package com.example.prj3be.service;

import com.example.prj3be.domain.Board;
import com.example.prj3be.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;

//    public List<Board> boardList(Board board) {
//        return boardRepository.findAll();
//    }

    public Page<Board> boardList(Pageable pageable) {
        return boardRepository.findAll(pageable);
    }

    public void save(Board board) {
        boardRepository.save(board);
    }

    public Optional<Board> getBoardById(Long id) {
        return boardRepository.findById(id);
    }

    public void update(Long id, Board updateBoard) {
        Optional<Board> boardById = boardRepository.findById(id);
        if( boardById.isPresent()) {
            Board board1 = boardById.get();
            board1.setTitle(updateBoard.getTitle());
            board1.setPrice(updateBoard.getPrice());
        }
    }


    public void delete(Long id) {
        boardRepository.deleteById(id);
    }





}
