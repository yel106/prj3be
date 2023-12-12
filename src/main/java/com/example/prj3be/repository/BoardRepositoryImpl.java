package com.example.prj3be.repository;

import com.example.prj3be.domain.*;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

import static com.example.prj3be.domain.QAlbumGenre.albumGenre;
import static com.example.prj3be.domain.QBoard.board;
import static com.querydsl.core.types.Order.ASC;
import static com.querydsl.core.types.Order.DESC;

@Repository
@RequiredArgsConstructor
public class BoardRepositoryImpl implements BoardRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    //구현 메소드 작성 queryDSL

    @Override
    public List<Board> searchAlbum(String keyword, Pageable pageable) {
        QBoard board = QBoard.board;
        QAlbumGenre albumGenre = QAlbumGenre.albumGenre;

        List<Board> query = queryFactory
                .select(board)
                .from(board)
                .leftJoin(board.albumGenres, albumGenre) //조인 관계 다시 확인하기
                .where(cdOption(AlbumFormat.CD).or(vinylOption(AlbumFormat.VINYL)).or(cassetteTapeOption(AlbumFormat.CASSETTETAPE)))
                .where(indieOption(AlbumDetail.INDIE), ostOption(AlbumDetail.OST), k_popOption(AlbumDetail.K_POP), popOption((AlbumDetail.POP)))
                .where(board.title.containsIgnoreCase(keyword)
                        .or(board.artist.containsIgnoreCase(keyword)
                                .or(board.agency.containsIgnoreCase(keyword)
                                        .or(board.content.containsIgnoreCase(keyword)))))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy()
                .fetch();

        return query;
    }


    //1차 분류: CD, VINYL, CASSETTETAPE
    //아래 방식 안되면 이 방식으로 다시 써보기
//    private BooleanExpression SelectedAlbumFormat(AlbumFormat albumFormat) {
//      if(albumFormat.equals("CD")) {
//          return filterByCD();
//      }
//    }
//    private BooleanExpression filterByCD() {};

    private BooleanExpression cdOption(AlbumFormat cdFormat) {
        if (cdFormat == null) {
            return null;
        }
        return board.albumFormat.eq(AlbumFormat.CD);
    }

    private BooleanExpression vinylOption(AlbumFormat vinylFormat) {
        if (vinylFormat == null) {
            return null;
        }
        return board.albumFormat.eq(AlbumFormat.VINYL);
    }

    private BooleanExpression cassetteTapeOption(AlbumFormat cassetteTapeFormat) {
        if (cassetteTapeFormat == null) {
            return null;
        }
        return board.albumFormat.eq(AlbumFormat.CASSETTETAPE);
    }

    // 2차 분류 :  INDIE, OST, K_POP, POP
    private BooleanExpression indieOption(AlbumDetail indieGenre) {
        if (indieGenre != null) {
            return albumGenre.albumDetail.eq(AlbumDetail.INDIE);
        } else return null;
    }

    private BooleanExpression ostOption(AlbumDetail ostGenre) {
        if (ostGenre != null) {
            return albumGenre.albumDetail.eq(AlbumDetail.OST);
        } else return null;
    }

    private BooleanExpression k_popOption(AlbumDetail k_popGenre) {
        if (k_popGenre != null) {
            return albumGenre.albumDetail.eq(AlbumDetail.K_POP);
        } else return null;
    }

    private BooleanExpression popOption(AlbumDetail popGenre) {
        if (popGenre != null) {
            return albumGenre.albumDetail.eq(AlbumDetail.POP);
        } else return null;
    }

    private OrderSpecifier[] getSortCondition(final Sort sort) {
        final List<OrderSpecifier> orders = new ArrayList<>();

        if(sort.isEmpty()) {
            return new OrderSpecifier[]{new OrderSpecifier(DESC, board.id)}
        }
    }

    private void method(final Sort.Order sortOder, final List<OrderSpecifier> orderSpecifiers) {
        //정렬 방식 지정할때 사용되는 열거형(enum) 타입
        com.querydsl.core.types.Order direction = DESC;
        if (sortOder.isAscending()) {
            direction = ASC;
        }




    }
