package com.example.prj3be.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QSocialToken is a Querydsl query type for SocialToken
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QSocialToken extends EntityPathBase<SocialToken> {

    private static final long serialVersionUID = 473128305L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QSocialToken socialToken = new QSocialToken("socialToken");

    public final StringPath accessToken = createString("accessToken");

    public final NumberPath<Integer> expiresIn = createNumber("expiresIn", Integer.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QMember member;

    public final StringPath refreshToken = createString("refreshToken");

    public final DateTimePath<java.time.LocalDateTime> regTime = createDateTime("regTime", java.time.LocalDateTime.class);

    public final EnumPath<com.example.prj3be.constant.SocialLoginType> socialLoginType = createEnum("socialLoginType", com.example.prj3be.constant.SocialLoginType.class);

    public final StringPath tokenType = createString("tokenType");

    public QSocialToken(String variable) {
        this(SocialToken.class, forVariable(variable), INITS);
    }

    public QSocialToken(Path<? extends SocialToken> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QSocialToken(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QSocialToken(PathMetadata metadata, PathInits inits) {
        this(SocialToken.class, metadata, inits);
    }

    public QSocialToken(Class<? extends SocialToken> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.member = inits.isInitialized("member") ? new QMember(forProperty("member")) : null;
    }

}

