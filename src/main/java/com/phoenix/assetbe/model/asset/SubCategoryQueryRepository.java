package com.phoenix.assetbe.model.asset;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.phoenix.assetbe.model.asset.QAssetSubCategory.assetSubCategory;
import static com.phoenix.assetbe.model.asset.QCategory.category;
import static com.phoenix.assetbe.model.asset.QSubCategory.subCategory;

@RequiredArgsConstructor
@Repository
public class SubCategoryQueryRepository {

    private final JPAQueryFactory queryFactory;

    /**
     * 서브 카테고리
     */
    public List<SubCategory> getSubCategoryByCategoryName(String categoryName) {
        List<SubCategory> result = queryFactory.select(subCategory)
                .from(assetSubCategory)
                .join(assetSubCategory.category, category)
                .join(assetSubCategory.subCategory, subCategory)
                .where(category.categoryName.eq(categoryName))
                .fetch();
        return result;
    }
}
