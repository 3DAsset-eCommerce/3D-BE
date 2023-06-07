package com.phoenix.assetbe.model.asset;

import com.phoenix.assetbe.dto.asset.AssetResponse;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class CategoryRepositoryImpl implements CategoryRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<AssetResponse.CountOutDTO.CountCategory> countByCategory() {
        QCategory c = QCategory.category;
        return queryFactory
                .select(Projections.constructor(AssetResponse.CountOutDTO.CountCategory.class, c.categoryName, c.categoryName.count()))
                .from(c)
                .groupBy(c.categoryName)
                .fetch();
    }

    @Override
    public List<AssetResponse.CountOutDTO.CountSubCategory> countBySubCategory() {
        QCategory c = QCategory.category;
        return queryFactory
                .select(Projections.constructor(AssetResponse.CountOutDTO.CountSubCategory.class, c.categoryName, c.subCategoryName, c.subCategoryName.count()))
                .from(c)
                .groupBy(c.subCategoryName)
                .fetch();
    }
}
