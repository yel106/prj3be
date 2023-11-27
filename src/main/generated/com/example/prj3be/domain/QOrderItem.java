package com.example.prj3be.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QOrderItem is a Querydsl query type for OrderItem
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QOrderItem extends EntityPathBase<OrderItem> {

    private static final long serialVersionUID = -429546586L;

    public static final QOrderItem orderItem = new QOrderItem("orderItem");

    public final NumberPath<Integer> count = createNumber("count", Integer.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Integer> orderPrice = createNumber("orderPrice", Integer.class);

    public QOrderItem(String variable) {
        super(OrderItem.class, forVariable(variable));
    }

    public QOrderItem(Path<? extends OrderItem> path) {
        super(path.getType(), path.getMetadata());
    }

    public QOrderItem(PathMetadata metadata) {
        super(OrderItem.class, metadata);
    }

}

