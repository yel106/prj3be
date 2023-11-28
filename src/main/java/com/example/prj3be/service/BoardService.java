package com.example.prj3be.service;

import com.example.prj3be.domain.Board;
import com.example.prj3be.domain.QBoard;
import com.example.prj3be.repository.BoardRepository;
import com.querydsl.core.types.Predicate;
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

    public Page<Board> boardList(Pageable pageable,
                                 String category,
                                 String keyword) {

//        Predicate predicate = QBoard.board.title.contains(keyword);
//                .or(QBoard.board.title.contains(keyword));

        QBoard board = QBoard.board;

        Predicate predicate = createPredicate(keyword, category, board); //호출시 param전달 순서도 일치해야함

        return boardRepository.findAll(predicate, pageable);


    }

    private Predicate createPredicate(String category, String keyword, QBoard board) {
        if ("all".equals(category)) {
            return board.title.containsIgnoreCase(keyword);
        } else if ("item".equals(category)) {
            return board.price.containsIgnoreCase(keyword);
        }
        return board.title.containsIgnoreCase(keyword);
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
