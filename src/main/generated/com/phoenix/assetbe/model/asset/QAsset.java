package com.phoenix.assetbe.model.asset;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QAsset is a Querydsl query type for Asset
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QAsset extends EntityPathBase<Asset> {

    private static final long serialVersionUID = 671435846L;

    public static final QAsset asset = new QAsset("asset");

    public final com.phoenix.assetbe.core.util.QMyTimeBaseUtil _super = new com.phoenix.assetbe.core.util.QMyTimeBaseUtil(this);

    public final StringPath assetName = createString("assetName");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final StringPath creator = createString("creator");

    public final NumberPath<Integer> discount = createNumber("discount", Integer.class);

    public final NumberPath<Double> discountPrice = createNumber("discountPrice", Double.class);

    public final StringPath extension = createString("extension");

    public final StringPath fileUrl = createString("fileUrl");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Double> price = createNumber("price", Double.class);

    public final NumberPath<Double> rating = createNumber("rating", Double.class);

    public final DatePath<java.time.LocalDate> releaseDate = createDate("releaseDate", java.time.LocalDate.class);

    public final NumberPath<Long> reviewCount = createNumber("reviewCount", Long.class);

    public final NumberPath<Double> size = createNumber("size", Double.class);

    public final BooleanPath status = createBoolean("status");

    public final StringPath thumbnailUrl = createString("thumbnailUrl");

    public final DateTimePath<java.time.LocalDateTime> updatedAt = createDateTime("updatedAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> visitCount = createNumber("visitCount", Long.class);

    public final NumberPath<Long> wishCount = createNumber("wishCount", Long.class);

    public QAsset(String variable) {
        super(Asset.class, forVariable(variable));
    }

    public QAsset(Path<? extends Asset> path) {
        super(path.getType(), path.getMetadata());
    }

    public QAsset(PathMetadata metadata) {
        super(Asset.class, metadata);
    }

}

