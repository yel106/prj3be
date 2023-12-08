package com.example.prj3be.repository;


import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class AlbumGenreRepositoryImpl implements AlbumGenreRepositoryCustom{
    private final JPAQueryFactory jpaQueryFactory;

//    @Override
    //구현 메소드 작성 queryDSL
}
