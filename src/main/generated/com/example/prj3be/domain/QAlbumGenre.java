package com.example.prj3be.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QAlbumGenre is a Querydsl query type for AlbumGenre
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QAlbumGenre extends EntityPathBase<AlbumGenre> {

    private static final long serialVersionUID = -2035922737L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QAlbumGenre albumGenre = new QAlbumGenre("albumGenre");

    public final EnumPath<AlbumDetail> albumDetail = createEnum("albumDetail", AlbumDetail.class);

    public final QBoard board;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public QAlbumGenre(String variable) {
        this(AlbumGenre.class, forVariable(variable), INITS);
    }

    public QAlbumGenre(Path<? extends AlbumGenre> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QAlbumGenre(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QAlbumGenre(PathMetadata metadata, PathInits inits) {
        this(AlbumGenre.class, metadata, inits);
    }

    public QAlbumGenre(Class<? extends AlbumGenre> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.board = inits.isInitialized("board") ? new QBoard(forProperty("board")) : null;
    }

}

