package com.example.prj3be.controller;

import com.example.prj3be.domain.Board;
import com.example.prj3be.domain.BoardFile;
import com.example.prj3be.service.BoardService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.List;
import java.util.Optional;

@RestController
//@RequiredArgsConstructor
@RequestMapping("/api/board")
public class BoardController {
    private final BoardService boardService;



    @GetMapping("list")
    public Page<Board> list(Pageable pageable,
                            @RequestParam Map<String, Object> params,
                            @RequestParam(value = "c", defaultValue = "all") String category,
                            @RequestParam(value="g", defaultValue = "all") String[] genre,
                            @RequestParam(value = "k", defaultValue = "") String keyword) {
        System.out.println("pageable = " + pageable);
        System.out.println("params = " + params);


        Page<Board> boardListPage = boardService.boardListAll(pageable, category, genre, keyword);

        // TODO: stackoverflowerror의 유력한 코드...
        return boardListPage;
    }


//    @PostMapping("add")
//    //파일 추가할때 @RequestBody X
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

    //희연이 코드
//    @GetMapping("id/{id}")
//    public Board get(@PathVariable Long id) {
//        return boardService.getBoardById(id).orElseThrow(() -> new EntityNotFoundException("Board not found with id: " + id));
//    }
    @GetMapping("id/{id}")
    public Optional<Board> get(@PathVariable Long id) {
        return boardService.getBoardById(id);
    }

    @PutMapping("/edit/{id}")
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





