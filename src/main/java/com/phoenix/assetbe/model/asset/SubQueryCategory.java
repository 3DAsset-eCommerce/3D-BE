package com.phoenix.assetbe.model.asset;

import com.phoenix.assetbe.dto.admin.AdminResponse;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.phoenix.assetbe.model.asset.QAssetSubCategory.assetSubCategory;
import static com.phoenix.assetbe.model.asset.QCategory.category;
import static com.phoenix.assetbe.model.asset.QSubCategory.subCategory;

@RequiredArgsConstructor
@Repository
public class SubQueryCategory {
    private final JPAQueryFactory queryFactory;

    public AdminResponse.GetSubCategoryListOutDTO getSubCategoryByCategoryName(String categoryName) {
        List<SubCategory> subCategories = queryFactory.select(subCategory)
                .from(assetSubCategory)
                .join(assetSubCategory.category, category)
                .join(assetSubCategory.subCategory, subCategory)
                .where(category.categoryName.eq(categoryName))
                .fetch();

        return new AdminResponse.GetSubCategoryListOutDTO(subCategories);
    }
    // .innerJoin(manager.areaManagers,areaManager).fetchJoin()
    //.innerJoin(areaManager.areaManagerKey.area,area).fetchJoin()
}
