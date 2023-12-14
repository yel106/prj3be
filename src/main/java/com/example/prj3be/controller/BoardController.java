package com.example.prj3be.controller;

import com.example.prj3be.domain.Board;
import com.example.prj3be.domain.BoardFile;
import com.example.prj3be.service.BoardService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/board")
public class BoardController {
    private final BoardService boardService;

    public BoardController(BoardService boardService) {
        this.boardService = boardService;
    }


    @GetMapping("list")
    public Page<Board> list(Pageable pageable,
                            @RequestParam(value = "c", defaultValue = "all") String category,
                            @RequestParam(value = "k", defaultValue = "") String keyword) {
        Page<Board> boardListPage = boardService.boardListAll(pageable, category, keyword);
        return boardListPage;
    }

    //파일 추가할때 @RequestBody X
//    public void add(@Validated Board saveBoard,
//                    @RequestParam(value = "uploadFiles[]", required = false) MultipartFile[] files,
//                    BoardFile boardFile) throws IOException {
//
//        boardService.save(saveBoard, files, boardFile);
//    }
    @PostMapping("add")
    public void add(@Validated Board saveBoard,
                    @RequestParam(value = "uploadFiles[]", required = false) MultipartFile[] files) throws IOException {
        System.out.println(saveBoard);
        boardService.save(saveBoard, files);
    }




    @GetMapping("id/{id}")
    public Optional<Board> get(@PathVariable Long id) {
        return boardService.getBoardById(id);
    }
    @GetMapping("file/id/{id}")
    public List<String> getURL(@PathVariable Long id) {
        return boardService.getBoardURL(id);
    }





    @PutMapping("/edit/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void update(@PathVariable Long id,
                       Board updateBboard,
                       @RequestParam(value = "uploadFiles", required = false) MultipartFile uploadFiles) throws IOException {
        System.out.println("updateBboard = " + updateBboard);
        System.out.println("uploadFiles = " + uploadFiles);

        if (uploadFiles == null) {
            boardService.update(id, updateBboard);

        } else {
            boardService.update(id, updateBboard, uploadFiles);
        }
    }

    @DeleteMapping("remove/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable Long id) {
        boardService.delete(id);
    }




    @GetMapping("file/id/{id}")
    public List<String> getURL(@PathVariable Long id) {
        return boardService.getBoardURL(id);
    }

    //희연이한테 물어보기
    public BoardController(BoardService boardService) {
        this.boardService = boardService;
    }


}