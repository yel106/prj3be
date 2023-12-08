//package com.example.prj3be;
//
//import com.example.prj3be.domain.AlbumDetail;
//import com.example.prj3be.domain.AlbumGenre;
//import com.example.prj3be.domain.Board;
//import com.example.prj3be.repository.AlbumGenreRepository;
//import com.example.prj3be.repository.BoardRepository;
//import jakarta.persistence.EntityManager;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import java.util.Optional;
//
////@DataJpaTest
//@SpringBootTest
//public class AlbumGereRepositoryTest {
//    @Autowired
//    private EntityManager em;
//
//    @Autowired
//    private AlbumGenreRepository repository;
//    @Autowired
//    private BoardRepository boardRepository;
//
//    @Test
//    public void method1() {
//        Optional<Board> byId = boardRepository.findById(4404L);
//        Board bts = byId.orElseThrow();
//        AlbumGenre genre2 = new AlbumGenre();
//        genre2.setId(79L);
//        genre2.setBoard(bts);
//        genre2.setAlbumDetail(AlbumDetail.K_POP);
//        repository.save(genre2);
//    }
//}
