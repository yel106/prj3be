package com.example.prj3be.repository;

import com.example.prj3be.domain.Board;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class BoardRepositoryImpl implements BoardRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    //구현 메소드 작성 queryDSL

//    @Override
//    public List<Board> searchAlbum(String keyword, Pageable pageable) {
//
//
//    }
}
