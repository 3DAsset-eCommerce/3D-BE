package com.phoenix.assetbe.model.asset;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPreview is a Querydsl query type for Preview
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPreview extends EntityPathBase<Preview> {

    private static final long serialVersionUID = 1391347614L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPreview preview = new QPreview("preview");

    public final com.phoenix.assetbe.core.util.QMyTimeBaseUtil _super = new com.phoenix.assetbe.core.util.QMyTimeBaseUtil(this);

    public final QAsset asset;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath previewUrl = createString("previewUrl");

    public QPreview(String variable) {
        this(Preview.class, forVariable(variable), INITS);
    }

    public QPreview(Path<? extends Preview> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QPreview(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QPreview(PathMetadata metadata, PathInits inits) {
        this(Preview.class, metadata, inits);
    }

    public QPreview(Class<? extends Preview> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.asset = inits.isInitialized("asset") ? new QAsset(forProperty("asset")) : null;
    }

}

