package com.phoenix.assetbe.model.asset;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QAssetTag is a Querydsl query type for AssetTag
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QAssetTag extends EntityPathBase<AssetTag> {

    private static final long serialVersionUID = 1082674548L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QAssetTag assetTag = new QAssetTag("assetTag");

    public final QAsset asset;

    public final QCategory category;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QSubCategory subCategory;

    public final QTag tag;

    public QAssetTag(String variable) {
        this(AssetTag.class, forVariable(variable), INITS);
    }

    public QAssetTag(Path<? extends AssetTag> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QAssetTag(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QAssetTag(PathMetadata metadata, PathInits inits) {
        this(AssetTag.class, metadata, inits);
    }

    public QAssetTag(Class<? extends AssetTag> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.asset = inits.isInitialized("asset") ? new QAsset(forProperty("asset")) : null;
        this.category = inits.isInitialized("category") ? new QCategory(forProperty("category")) : null;
        this.subCategory = inits.isInitialized("subCategory") ? new QSubCategory(forProperty("subCategory")) : null;
        this.tag = inits.isInitialized("tag") ? new QTag(forProperty("tag")) : null;
    }

}

