package com.phoenix.assetbe.model.asset;

import com.phoenix.assetbe.dto.asset.CategoryResponse;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.phoenix.assetbe.model.asset.QAsset.asset;
import static com.phoenix.assetbe.model.asset.QAssetTag.assetTag;
import static com.phoenix.assetbe.model.asset.QCategory.category;
import static com.phoenix.assetbe.model.asset.QPreview.preview;
import static com.phoenix.assetbe.model.asset.QSubCategory.subCategory;
import static com.phoenix.assetbe.model.asset.QTag.tag;

@RequiredArgsConstructor
@Repository
public class AssetTagQueryRepository {

    private final JPAQueryFactory queryFactory;

    public List<String> findTagNameListByAssetId(Long assetId) {
        return queryFactory.selectDistinct(assetTag.tag.tagName)
                .from(assetTag)
                .join(assetTag.tag, tag)
                .where(assetTag.asset.id.eq(assetId))
                .orderBy(tag.tagName.asc())
                .fetch();
    }

    public List<CategoryResponse.CategoryOutDTO.CountByCategory> findAssetCountByCategory() {
        return queryFactory
                .select(Projections.constructor(CategoryResponse.CategoryOutDTO.CountByCategory.class,
                        assetTag.category.categoryName,
                        assetTag.asset.id.countDistinct()))
                .from(assetTag)
                .leftJoin(category).on(category.id.eq(assetTag.category.id))
                .groupBy(category.categoryName)
                .fetch();
    }

    public List<CategoryResponse.CategoryOutDTO.CountBySubCategory> findAssetCountBySubCategory() {
        return queryFactory
                .select(Projections.constructor(CategoryResponse.CategoryOutDTO.CountBySubCategory.class,
                                assetTag.category.categoryName,
                                assetTag.subCategory.subCategoryName,
                                assetTag.asset.id.countDistinct()))
                .from(assetTag)
                .leftJoin(category).on(category.id.eq(assetTag.category.id))
                .leftJoin(subCategory).on(subCategory.id.eq(assetTag.subCategory.id))
                .groupBy(category.categoryName, subCategory.subCategoryName)
                .fetch();
    }

    public List<CategoryResponse.CategoryOutDTO.CountByTag> findAssetCountByTag() {
        return queryFactory
                .select(Projections.constructor(CategoryResponse.CategoryOutDTO.CountByTag.class,
                        assetTag.category.categoryName,
                        assetTag.subCategory.subCategoryName,
                        assetTag.tag.tagName,
                        assetTag.asset.countDistinct()))
                .from(assetTag)
                .leftJoin(category).on(category.id.eq(assetTag.category.id))
                .leftJoin(subCategory).on(subCategory.id.eq(assetTag.subCategory.id))
                .leftJoin(tag).on(tag.id.eq(assetTag.tag.id))
                .groupBy(category.categoryName, subCategory.subCategoryName, tag.tagName)
                .fetch();
    }
}
