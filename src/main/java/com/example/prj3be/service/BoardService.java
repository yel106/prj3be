package com.example.prj3be.service;

import com.example.prj3be.domain.*;

import com.example.prj3be.repository.BoardFileRepository;
import com.example.prj3be.repository.BoardRepository;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.example.prj3be.domain.QAlbumGenre.albumGenre;
import static com.example.prj3be.domain.QBoard.board;

@Service
@Transactional
//@RequiredArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;
    private final BoardFileRepository boardFileRepository;

    @Value("${image.file.prefix}")
    private String urlPrefix;

    @Value("${aws.s3.bucket.name}")
    private String bucket;

    private final S3Client s3;


//    public Page<Board> boardListAll(Pageable pageable, String category, String[] genre, String keyword) {


//        QBoard board = QBoard.board;
//        BooleanBuilder builder = new BooleanBuilder();
//
//        if (category != null && keyword != null) {
//            if ("all".equals(category)) {
//                builder.and(board.title.containsIgnoreCase(keyword));
//            } else if ("CD".equals(category)) {
//                builder.and(board.albumFormat.eq(AlbumFormat.CD));
//                builder.and(board.title.containsIgnoreCase(keyword));
//            } else if ("vinyl".equals(category)) {
//                builder.and(board.albumFormat.eq(AlbumFormat.VINYL));
//                builder.and(board.title.containsIgnoreCase(keyword));
//            } else if ("cassettetape".equals(category)) {
//                builder.and(board.albumFormat.eq(AlbumFormat.CASSETTETAPE));
//                builder.and(board.title.containsIgnoreCase(keyword));
//            }
//
//        }
//
////         any(): List컬렉션 내의 요소들에 대한 조건 설정
////        if (!genre.) {
//            if ("all".equals(genre)) {
//                builder.and(board.title.containsIgnoreCase(keyword));
//            } else if ("INDIE".equals(genre)) {
//                builder.andAnyOf(board.albumGenres.any().albumDetail.eq(AlbumDetail.INDIE));
//            } else if ("OST".equals(genre)) {
//                builder.andAnyOf(board.albumGenres.any().albumDetail.eq(AlbumDetail.OST));
//            } else if ("K_POP".equals(genre)) {
//                builder.andAnyOf(board.albumGenres.any().albumDetail.eq(AlbumDetail.K_POP));
//            } else if ("POP".equals(genre)) {
//                builder.andAnyOf(board.albumGenres.any().albumDetail.eq(AlbumDetail.POP));
//            }
//
//
//        Predicate predicate = builder.hasValue() ? builder.getValue() : null; //삼항연산자
//
//        if (predicate != null) {
//            return boardRepository.findAll(predicate, pageable);
//        } else {
//            return boardRepository.findAll(pageable);
//        }
//    }

    //새로 만들고 있는 부분
//    public Page<List<Board>> boardListAll(AlbumFormat albumFormat, String keyword) {
//        return boardRepository.searchAlbum(AlbumFormat albumFormat, String keyword);
//    }


    public void save(Board board, MultipartFile[] files) throws IOException {
        board.setId(10L);
        boardRepository.save(board); //jpa의 save()메소드엔 파일을 넣지 못함

        Long id = board.getId();
        BoardFile boardFile = new BoardFile();
        Optional<Board> findBoard = boardRepository.findById(id);
        Board savedBoard = findBoard.get();


        for (int i = 0; i < files.length; i++) {
            String url = urlPrefix + "prj3/"+ id +"/" + files[i].getOriginalFilename();
            boardFile.setFileName(files[i].getOriginalFilename());
            boardFile.setFileUrl(url);
            boardFile.setBoard(savedBoard);
            boardFileRepository.save(boardFile);    //boardFile 테이블에 files 정보(fileName, fileUrl) 저장
            upload(files[i], id);
        }
    }

    //AWS s3에 파일 업로드
    private void upload(MultipartFile file,Long id) throws IOException {
        String key = "prj3/" + id + "/" +file.getOriginalFilename();

        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .acl(ObjectCannedACL.PUBLIC_READ)
                .build();

        s3.putObject(objectRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
    }

    public String get(Long id, BoardFile boardFile) {


//        id로 board에 있는 id, title, price값을 가져와서 board에 넣음
        Optional<Board> board = boardRepository.findById(id);

        //파일 table에서 id로 파일명을 알아온 후에 boadFiles에 집어넣음
//        Optional<BoardFile> boardFiles = boardFileRepository.findById(id);

        //Optional은 foreach 사용 불가. Optional은 set메소드 이용해서 내부의 값 직접 설정할수 없음
        // optional -> get메소드
//        if (boardFiles.isPresent()) {
//            BoardFile boardFile1 = boardFiles.get(); //보드 파일이 존재한다면 파일에 있는걸 boardFile1에 넣음
//            String url = urlPrefix + "prj3/"+ id +"/" + boardFile1.getFileName(); //보드파일1의 파일name을 url에 넣음
//            boardFile1.setFileUrl(url); //boardFile1에 setter로 FileUrl필드에 url값을 집어넣음
//            String fileUrl = boardFile1.getFileUrl();  //boardFile에 들어간 url값을 fileUrl변수에 넣음
//            return fileUrl;
//        } else {
//            return null;
//        }
        return "ddd";
    }

    public Optional<Board> getBoardById(Long id) {
        return boardRepository.findById(id);
    }

    public Board update(Long id, Board updateBoard) {
        Optional<Board> boardById = boardRepository.findById(id);
        if (boardById.isPresent()) {
            Board board1 = boardById.get();
            board1.setTitle(updateBoard.getTitle());
            board1.setArtist(updateBoard.getArtist());
            board1.setPrice(updateBoard.getPrice());
            board1.setReleaseDate(updateBoard.getReleaseDate());
            board1.setContent(updateBoard.getContent());

//            앨범 포멧은 변경할 수 없는 걸로 해서 추가 안했어요.
            return boardRepository.save(board1);
        }
        return null;
    }

    public void update(Long id, Board updateBboard, MultipartFile uploadFiles) throws IOException {
        Board updatedBoard = update(id, updateBboard);
//        boardFileRepository.deleteBoardFileByBoardId(id);

        BoardFile boardFile = new BoardFile();
        String url = urlPrefix + "prj3/"+ id +"/" + uploadFiles.getOriginalFilename();
        boardFile.setFileName(uploadFiles.getOriginalFilename());
        boardFile.setFileUrl(url);
        boardFile.setBoard(updatedBoard);
        boardFileRepository.save(boardFile);    //boardFile 테이블에 files 정보(fileName, fileUrl) 저장
        upload(uploadFiles, id);

    }

    public void delete(Long id) {
        boardFileRepository.deleteBoardFileByBoardId(id);
        boardRepository.deleteById(id);
    }

    public List<String> getBoardURL(Long id) {
        return boardFileRepository.findFileUrlsByBoardId(id);
    }


    public BoardService(BoardRepository boardRepository,BoardFileRepository boardFileRepository, S3Client s3){
        this.boardRepository = boardRepository;
        this.boardFileRepository = boardFileRepository;
        this.s3 = s3;
    }

//    public void save(Board saveBoard, String imageURL) {
//        saveBoard.setImageURL(imageURL);
//        BoardFile boardFile = new BoardFile();
//        boardFile.setFileName(saveBoard.getFileName());
//        boardFile.setFileUrl(imageURL);
//        boardRepository.save(saveBoard);
//        boardFileRepository.save(boardFile);
//    }



//    public void saveWithImageURL(Board saveBoard, String imageURL) {
//        saveBoard.setImageURL(imageURL);
//        boardFileRepository.save(board);
//    }
}
