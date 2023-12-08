//package com.example.prj3be.repository;
//
//import com.example.prj3be.domain.Board;
//import com.example.prj3be.domain.QBoard;
//import com.querydsl.core.BooleanBuilder;
//import com.querydsl.jpa.impl.JPAQueryFactory;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Repository;
//
//import java.util.List;
//
//@Repository
//@RequiredArgsConstructor
//public class BoardRepositoryCustom() {
//    private JPAQueryFactory jpaQueryFactory;
//    public List<Board> search() {
//        BooleanBuilder builder = new BooleanBuilder();
//        //조건문
//        return jpaQueryFactory.selectFrom(QBoard.board).where(builder).fetch();
//    }
//}
