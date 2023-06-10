package com.phoenix.assetbe.model.asset;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMyAsset is a Querydsl query type for MyAsset
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMyAsset extends EntityPathBase<MyAsset> {

    private static final long serialVersionUID = -1104085894L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QMyAsset myAsset = new QMyAsset("myAsset");

    public final com.phoenix.assetbe.core.util.QMyTimeBaseUtil _super = new com.phoenix.assetbe.core.util.QMyTimeBaseUtil(this);

    public final QAsset asset;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final com.phoenix.assetbe.model.user.QUser user;

    public QMyAsset(String variable) {
        this(MyAsset.class, forVariable(variable), INITS);
    }

    public QMyAsset(Path<? extends MyAsset> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QMyAsset(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QMyAsset(PathMetadata metadata, PathInits inits) {
        this(MyAsset.class, metadata, inits);
    }

    public QMyAsset(Class<? extends MyAsset> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.asset = inits.isInitialized("asset") ? new QAsset(forProperty("asset")) : null;
        this.user = inits.isInitialized("user") ? new com.phoenix.assetbe.model.user.QUser(forProperty("user")) : null;
    }

}

