package com.example.prj3be.controller;

import com.example.prj3be.domain.Board;
import com.example.prj3be.domain.BoardFile;
import com.example.prj3be.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/board")
public class BoardController {
    private final BoardService boardService;

    @GetMapping("list")
    public Page<Board> list(Pageable pageable,
                            @RequestParam(value = "c", defaultValue = "all") String category,
                            @RequestParam(value = "k", defaultValue = "") String keyword) {
        Page<Board> boardListPage = boardService.boardListAll(pageable, category, keyword);
        return boardListPage;
    }


    //파일 추가할때 @RequestBody X
    public void add(@Validated Board saveBoard,
                    @RequestParam(value = "uploadFiles[]", required = false) MultipartFile[] files,
                    BoardFile boardFile) throws IOException {

        boardService.save(saveBoard, files, boardFile);
    }
    @PostMapping("add")
    public void add(@Validated Board saveBoard,
                    @RequestParam(value = "imageURL") String imageURL) {
        System.out.println(saveBoard);
        boardService.save(saveBoard, imageURL);
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