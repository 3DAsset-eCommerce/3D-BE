package com.phoenix.assetbe.model.asset;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QAssetSubCategory is a Querydsl query type for AssetSubCategory
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QAssetSubCategory extends EntityPathBase<AssetSubCategory> {

    private static final long serialVersionUID = -1622363336L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QAssetSubCategory assetSubCategory = new QAssetSubCategory("assetSubCategory");

    public final QAsset asset;

    public final QCategory category;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QSubCategory subCategory;

    public QAssetSubCategory(String variable) {
        this(AssetSubCategory.class, forVariable(variable), INITS);
    }

    public QAssetSubCategory(Path<? extends AssetSubCategory> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QAssetSubCategory(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QAssetSubCategory(PathMetadata metadata, PathInits inits) {
        this(AssetSubCategory.class, metadata, inits);
    }

    public QAssetSubCategory(Class<? extends AssetSubCategory> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.asset = inits.isInitialized("asset") ? new QAsset(forProperty("asset")) : null;
        this.category = inits.isInitialized("category") ? new QCategory(forProperty("category")) : null;
        this.subCategory = inits.isInitialized("subCategory") ? new QSubCategory(forProperty("subCategory")) : null;
    }

}

