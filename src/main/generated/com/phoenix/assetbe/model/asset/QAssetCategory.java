package com.phoenix.assetbe.model.asset;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QAssetCategory is a Querydsl query type for AssetCategory
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QAssetCategory extends EntityPathBase<AssetCategory> {

    private static final long serialVersionUID = 898418532L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QAssetCategory assetCategory = new QAssetCategory("assetCategory");

    public final QAsset asset;

    public final QCategory category;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public QAssetCategory(String variable) {
        this(AssetCategory.class, forVariable(variable), INITS);
    }

    public QAssetCategory(Path<? extends AssetCategory> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QAssetCategory(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QAssetCategory(PathMetadata metadata, PathInits inits) {
        this(AssetCategory.class, metadata, inits);
    }

    public QAssetCategory(Class<? extends AssetCategory> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.asset = inits.isInitialized("asset") ? new QAsset(forProperty("asset")) : null;
        this.category = inits.isInitialized("category") ? new QCategory(forProperty("category")) : null;
    }

}

