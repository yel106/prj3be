package com.example.prj3be.service;

import com.example.prj3be.domain.Board;
import com.example.prj3be.domain.QBoard;
import com.example.prj3be.repository.BoardRepository;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;

    public Page<Board> boardListAll(Pageable pageable, String category, String keyword) {
        QBoard board = QBoard.board;
        Predicate predicate = createPredicate(category, keyword, board);

        return boardRepository.findAll(predicate, pageable);
    }


    private Predicate createPredicate(String category, String keyword, QBoard board) {
        BooleanBuilder builder = new BooleanBuilder();

        if( keyword != null && !keyword.trim().isEmpty()) {
            builder.and(board.title.containsIgnoreCase(keyword));
        }

//        if(!"all".equals(category)) {
//            builder.and(board.title.containsIgnoreCase(keyword));
//        }
        return builder;
    }


    public void save(Board board) {
        boardRepository.save(board);
    }

    public Optional<Board> getBoardById(Long id) {
        return boardRepository.findById(id);
    }

    public void update(Long id, Board updateBoard) {
        Optional<Board> boardById = boardRepository.findById(id);
        if (boardById.isPresent()) {
            Board board1 = boardById.get();
            board1.setTitle(updateBoard.getTitle());
            board1.setPrice(updateBoard.getPrice());
        }
    }

    public void delete(Long id) {
        boardRepository.deleteById(id);
    }


}
