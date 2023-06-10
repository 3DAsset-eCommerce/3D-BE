package com.phoenix.assetbe.core.util;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QMyTimeBaseUtil is a Querydsl query type for MyTimeBaseUtil
 */
@Generated("com.querydsl.codegen.DefaultSupertypeSerializer")
public class QMyTimeBaseUtil extends EntityPathBase<MyTimeBaseUtil> {

    private static final long serialVersionUID = 1861681040L;

    public static final QMyTimeBaseUtil myTimeBaseUtil = new QMyTimeBaseUtil("myTimeBaseUtil");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public QMyTimeBaseUtil(String variable) {
        super(MyTimeBaseUtil.class, forVariable(variable));
    }

    public QMyTimeBaseUtil(Path<? extends MyTimeBaseUtil> path) {
        super(path.getType(), path.getMetadata());
    }

    public QMyTimeBaseUtil(PathMetadata metadata) {
        super(MyTimeBaseUtil.class, metadata);
    }

}

