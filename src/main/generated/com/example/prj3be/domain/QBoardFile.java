package com.example.prj3be.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QBoardFile is a Querydsl query type for BoardFile
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QBoardFile extends EntityPathBase<BoardFile> {

    private static final long serialVersionUID = -1644299865L;

    public static final QBoardFile boardFile = new QBoardFile("boardFile");

    public final StringPath fileName = createString("fileName");

    public final StringPath fileUrl = createString("fileUrl");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public QBoardFile(String variable) {
        super(BoardFile.class, forVariable(variable));
    }

    public QBoardFile(Path<? extends BoardFile> path) {
        super(path.getType(), path.getMetadata());
    }

    public QBoardFile(PathMetadata metadata) {
        super(BoardFile.class, metadata);
    }

}

