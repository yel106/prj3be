package com.example.prj3be.controller;

import com.example.prj3be.domain.AlbumDetail;
import com.example.prj3be.domain.AlbumFormat;
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
import java.util.Arrays;
import java.util.Map;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
//@RequiredArgsConstructor
@RequestMapping("/api/board")
public class BoardController {
    private final BoardService boardService;


    @GetMapping("list")
    public Page<Board> list(Pageable pageable,
                            @RequestParam(required = false) String title,
                            @RequestParam(required = false) AlbumFormat albumFormat,
                            @RequestParam(required = false) String[] albumDetails,
                            @RequestParam(required = false) String minPrice,
                            @RequestParam(required = false) String maxPrice) {

        List<AlbumDetail> albumDetailList = (albumDetails == null) ? null : Arrays.stream(albumDetails).map(AlbumDetail::valueOf).collect(Collectors.toList());

        Page<Board> boardListPage = boardService.boardListAll(pageable, title, albumFormat, albumDetailList, minPrice, maxPrice);
        return boardListPage;
    }


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





