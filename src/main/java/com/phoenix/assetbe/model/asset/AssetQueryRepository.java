package com.phoenix.assetbe.model.asset;

import com.phoenix.assetbe.dto.asset.AssetResponse;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.*;
import com.querydsl.core.types.dsl.*;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;

import static com.phoenix.assetbe.model.asset.QAsset.asset;
import static com.phoenix.assetbe.model.asset.QAssetCategory.assetCategory;
import static com.phoenix.assetbe.model.asset.QAssetTag.assetTag;
import static com.phoenix.assetbe.model.asset.QCategory.category;
import static com.phoenix.assetbe.model.asset.QSubCategory.subCategory;
import static com.phoenix.assetbe.model.asset.QTag.tag;
import static com.phoenix.assetbe.model.cart.QCart.cart;
import static com.phoenix.assetbe.model.wish.QWishList.wishList;

@RequiredArgsConstructor
@Repository
public class AssetQueryRepository {

    private final JPAQueryFactory queryFactory;

    /**
     * 로그인 유저
     * 개별 에셋
     * 에셋 검색
     * 페이지네이션
     */
    public Page<AssetResponse.AssetListOutDTO.AssetOutDTO> findAssetListWithUser(
            Long userId, List<String> keywordList, Pageable pageable){

        List<AssetResponse.AssetListOutDTO.AssetOutDTO> result = queryFactory
                .selectDistinct(Projections.constructor(AssetResponse.AssetListOutDTO.AssetOutDTO.class,
                        asset.id,
                        asset.assetName,
                        asset.price,
                        asset.discount,
                        asset.discountPrice,
                        asset.releaseDate,
                        asset.thumbnailUrl,
                        asset.rating,
                        asset.reviewCount,
                        asset.wishCount,
                        wishList.id,
                        cart.id)
                )
                .from(asset)
                .leftJoin(wishList).on(wishList.user.id.eq(userId).and(wishList.asset.eq(asset)))
                .leftJoin(cart).on(cart.user.id.eq(userId).and(cart.asset.eq(asset)))
                .where(asset.status.eq(true), containKeyword(keywordList))
                .orderBy(assetSortByIncludedKeywordCount(keywordList).desc(), assetSort(pageable))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long totalCount = queryFactory.select(asset.count())
                .from(asset)
                .where(asset.status.eq(true), containKeyword(keywordList))
                .fetchOne();

        return new PageImpl<>(result, pageable, totalCount);
    }

    /**
     * 로그인 유저
     * 개별 에셋
     * 에셋 검색
     * 페이지네이션
     */
    public Page<AssetResponse.AssetListOutDTO.AssetOutDTO> findAssetList(
            List<String> keywordList, Pageable pageable){

        List<AssetResponse.AssetListOutDTO.AssetOutDTO> result = queryFactory
                .selectDistinct(Projections.constructor(AssetResponse.AssetListOutDTO.AssetOutDTO.class,
                        asset.id,
                        asset.assetName,
                        asset.price,
                        asset.discount,
                        asset.discountPrice,
                        asset.releaseDate,
                        asset.thumbnailUrl,
                        asset.rating,
                        asset.reviewCount,
                        asset.wishCount)
                )
                .from(asset)
                .where(asset.status.eq(true), containKeyword(keywordList))
                .orderBy(assetSortByIncludedKeywordCount(keywordList).desc(), assetSort(pageable))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long totalCount = queryFactory.select(asset.id.countDistinct())
                .from(asset)
                .where(asset.status.eq(true), containKeyword(keywordList))
                .fetchOne();

        return new PageImpl<>(result, pageable, totalCount);
    }

    /**
     * 로그인 유저
     * 카테고리별 에셋 조회 및 검색
     * 서브카테고리별 에셋 조회 및 검색
     * 검색조건: tag, keyword
     * 페이지네이션
     */
    public Page<AssetResponse.AssetListOutDTO.AssetOutDTO> findAssetListWithUserByCategoryOrSubCategory(
            Long userId, String categoryName, String subCategoryName, List<String> tagList, List<String> keywordList, Pageable pageable){

        List <AssetResponse.AssetListOutDTO.AssetOutDTO> result = queryFactory
                .selectDistinct(Projections.constructor(AssetResponse.AssetListOutDTO.AssetOutDTO.class,
                        asset.id,
                        asset.assetName,
                        asset.price,
                        asset.discount,
                        asset.discountPrice,
                        asset.releaseDate,
                        asset.thumbnailUrl,
                        asset.rating,
                        asset.reviewCount,
                        asset.wishCount,
                        wishList.id,
                        cart.id)
                )
                .from(asset)
                .innerJoin(assetTag).on(asset.id.eq(assetTag.asset.id))
                .innerJoin(category).on(category.id.eq(assetTag.category.id))
                .innerJoin(subCategory).on(subCategory.id.eq(assetTag.subCategory.id))
                .innerJoin(tag).on(tag.id.eq(assetTag.tag.id))
                .leftJoin(wishList).on(wishList.user.id.eq(userId).and(wishList.asset.eq(asset)))
                .leftJoin(cart).on(cart.user.id.eq(userId).and(cart.asset.eq(asset)))
                .where(asset.status.eq(true), categoryNameEq(categoryName), subCategoryNameEq(subCategoryName), tagListEq(tagList), containKeyword(keywordList))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(assetSortByIncludedKeywordCount(keywordList).desc(), assetSort(pageable))
                .fetch();

        Long totalCount = queryFactory.select(asset.id.countDistinct())
                .from(asset)
                .innerJoin(assetTag).on(asset.id.eq(assetTag.asset.id))
                .innerJoin(category).on(category.id.eq(assetTag.category.id))
                .innerJoin(subCategory).on(subCategory.id.eq(assetTag.subCategory.id))
                .innerJoin(tag).on(tag.id.eq(assetTag.tag.id))
                .where(asset.status.eq(true), categoryNameEq(categoryName), subCategoryNameEq(subCategoryName), tagListEq(tagList), containKeyword(keywordList))
                .fetchOne();

        return new PageImpl<>(result, pageable, totalCount);
    }

    /**
     * 비로그인 유저
     * 카테고리별 에셋 조회 및 검색
     * 서브카테고리별 에셋 조회 및 검색
     * 검색조건: tag, keyword
     * 페이지네이션
     */
    public Page<AssetResponse.AssetListOutDTO.AssetOutDTO> findAssetListByCategoryOrSubCategory(
            String categoryName, String subCategoryName, List<String> tagList, List<String> keywordList, Pageable pageable){

        List <AssetResponse.AssetListOutDTO.AssetOutDTO> result = queryFactory
                .selectDistinct(Projections.constructor(AssetResponse.AssetListOutDTO.AssetOutDTO.class,
                        asset.id,
                        asset.assetName,
                        asset.price,
                        asset.discount,
                        asset.discountPrice,
                        asset.releaseDate,
                        asset.thumbnailUrl,
                        asset.rating,
                        asset.reviewCount,
                        asset.wishCount)
                )
                .from(asset)
                .innerJoin(assetTag).on(asset.id.eq(assetTag.asset.id))
                .innerJoin(category).on(category.id.eq(assetTag.category.id))
                .innerJoin(subCategory).on(subCategory.id.eq(assetTag.subCategory.id))
                .innerJoin(tag).on(tag.id.eq(assetTag.tag.id))
                .where(asset.status.eq(true), categoryNameEq(categoryName), subCategoryNameEq(subCategoryName), tagListEq(tagList), containKeyword(keywordList))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(assetSortByIncludedKeywordCount(keywordList).desc(), assetSort(pageable))
                .fetch();

        Long totalCount = queryFactory.select(asset.id.countDistinct())
                .from(asset)
                .innerJoin(assetTag).on(asset.id.eq(assetTag.asset.id))
                .innerJoin(category).on(category.id.eq(assetTag.category.id))
                .innerJoin(subCategory).on(subCategory.id.eq(assetTag.subCategory.id))
                .innerJoin(tag).on(tag.id.eq(assetTag.tag.id))
                .where(asset.status.eq(true), categoryNameEq(categoryName), subCategoryNameEq(subCategoryName), tagListEq(tagList), containKeyword(keywordList))
                .fetchOne();

        return new PageImpl<>(result, pageable, totalCount);
    }

    public boolean existsAssetByAssetId(Long assetId) {
        Integer fetchOne = queryFactory
                .selectOne()
                .from(asset)
                .where(asset.status.eq(true).and(asset.id.eq(assetId)))
                .fetchFirst(); // limit 1
        return fetchOne != null; // 1개가 있는지 없는지 판단 (없으면 null 이므로 null 체크)
    }

    private BooleanExpression categoryNameEq(String categoryName) {
        return StringUtils.hasText(categoryName) ? category.categoryName.eq(categoryName) : null;
    }

    private BooleanExpression subCategoryNameEq(String subCategoryName) {
        return StringUtils.hasText(subCategoryName) ? subCategory.subCategoryName.eq(subCategoryName) : null;
    }

    private BooleanExpression tagNameEq(String tagName){
        return StringUtils.hasText(tagName) ? tag.tagName.eq(tagName) : null;
    }

    private BooleanExpression assetNameLike(String keyword){
        return StringUtils.hasText(keyword) ? asset.assetName.containsIgnoreCase(keyword) : null;
    }

    private BooleanBuilder tagListEq(List<String> tagList){
        BooleanBuilder builder = new BooleanBuilder();

        if(tagList == null || tagList.isEmpty()){
            return null;
        }

        for(String tag : tagList){
            builder.and(tagNameEq(tag));
        }
        return builder;
    }

    private BooleanBuilder containKeyword(List<String> splitKeywordList){
        BooleanBuilder builder = new BooleanBuilder();

        if(splitKeywordList == null || splitKeywordList.isEmpty()){
            return null;
        }

        for(String keyword : splitKeywordList){
            builder.or(assetNameLike(keyword));
        }
        return builder;
    }

    /**
     * 정렬 기준
     */
    private OrderSpecifier<?> assetSort(Pageable pageable) {

        if (!pageable.getSort().isEmpty()) {
            for (Sort.Order order : pageable.getSort()) {
                Order direction = order.getDirection().isAscending() ? Order.ASC : Order.DESC;
                switch (order.getProperty()){
                    case "id":
                        return new OrderSpecifier<>(direction, asset.id);
                    case "assetName":
                        return new OrderSpecifier<>(direction, asset.assetName);
                    case "price":
                        return new OrderSpecifier<>(direction, asset.price);
                    case "releaseDate":
                        return new OrderSpecifier<>(direction, asset.releaseDate);
                    case "rating":
                        return new OrderSpecifier<>(direction, asset.rating);
                    case "reviewCount":
                        return new OrderSpecifier<>(direction, asset.reviewCount);
                    case "wishCount":
                        return new OrderSpecifier<>(direction, asset.wishCount);
                }
            }
        }
        return null;
    }

    /**
     * 포함된 키워드 수 순서로 정렬, 키워드 4개이상 부터는 정렬기준 동일.
     */
    private NumberExpression<Integer> assetSortByIncludedKeywordCount(List<String> keywordList){

        NumberExpression<Integer> expression;

        if(keywordList == null || keywordList.isEmpty()){
            expression = new CaseBuilder()
                    .when(asset.assetName.isNotNull()).then(1)
                    .otherwise(0);
        } else if (keywordList.size() == 1) {
            expression = new CaseBuilder()
                    .when(asset.assetName.containsIgnoreCase(keywordList.get(0))).then(1)
                    .otherwise(0);
        } else if (keywordList.size() == 2) {
            expression = new CaseBuilder()
                    .when(asset.assetName.containsIgnoreCase(keywordList.get(0))
                            .and(asset.assetName.containsIgnoreCase(keywordList.get(1)))).then(3)
                    .when(asset.assetName.containsIgnoreCase(keywordList.get(0))).then(2)
                    .when(asset.assetName.containsIgnoreCase(keywordList.get(1))).then(1)
                    .otherwise(0);
        } else if (keywordList.size() == 3) {
            expression = new CaseBuilder()
                    .when(asset.assetName.containsIgnoreCase(keywordList.get(0))
                            .and(asset.assetName.containsIgnoreCase(keywordList.get(1)))
                            .and(asset.assetName.containsIgnoreCase(keywordList.get(2)))).then(7)
                    .when(asset.assetName.containsIgnoreCase(keywordList.get(0))
                            .and(asset.assetName.containsIgnoreCase(keywordList.get(1)))).then(6)
                    .when(asset.assetName.containsIgnoreCase(keywordList.get(0))
                            .and(asset.assetName.containsIgnoreCase(keywordList.get(2)))).then(5)
                    .when(asset.assetName.containsIgnoreCase(keywordList.get(1))
                            .and(asset.assetName.containsIgnoreCase(keywordList.get(2)))).then(4)
                    .when(asset.assetName.containsIgnoreCase(keywordList.get(0))).then(3)
                    .when(asset.assetName.containsIgnoreCase(keywordList.get(1))).then(2)
                    .when(asset.assetName.containsIgnoreCase(keywordList.get(2))).then(1)
                    .otherwise(0);
        } else {
            expression = new CaseBuilder()
                    .when(asset.assetName.containsIgnoreCase(keywordList.get(0))
                            .and(asset.assetName.containsIgnoreCase(keywordList.get(1)))
                            .and(asset.assetName.containsIgnoreCase(keywordList.get(2)))
                            .and(asset.assetName.containsIgnoreCase(keywordList.get(3)))).then(15)
                    .when(asset.assetName.containsIgnoreCase(keywordList.get(0))
                            .and(asset.assetName.containsIgnoreCase(keywordList.get(1)))
                            .and(asset.assetName.containsIgnoreCase(keywordList.get(2)))).then(14)
                    .when(asset.assetName.containsIgnoreCase(keywordList.get(0))
                            .and(asset.assetName.containsIgnoreCase(keywordList.get(1)))
                            .and(asset.assetName.containsIgnoreCase(keywordList.get(3)))).then(13)
                    .when(asset.assetName.containsIgnoreCase(keywordList.get(0))
                            .and(asset.assetName.containsIgnoreCase(keywordList.get(2)))
                            .and(asset.assetName.containsIgnoreCase(keywordList.get(3)))).then(12)
                    .when(asset.assetName.containsIgnoreCase(keywordList.get(1))
                            .and(asset.assetName.containsIgnoreCase(keywordList.get(2)))
                            .and(asset.assetName.containsIgnoreCase(keywordList.get(3)))).then(11)
                    .when(asset.assetName.containsIgnoreCase(keywordList.get(0))
                            .and(asset.assetName.containsIgnoreCase(keywordList.get(1)))).then(10)
                    .when(asset.assetName.containsIgnoreCase(keywordList.get(0))
                            .and(asset.assetName.containsIgnoreCase(keywordList.get(2)))).then(9)
                    .when(asset.assetName.containsIgnoreCase(keywordList.get(0))
                            .and(asset.assetName.containsIgnoreCase(keywordList.get(3)))).then(8)
                    .when(asset.assetName.containsIgnoreCase(keywordList.get(1))
                            .and(asset.assetName.containsIgnoreCase(keywordList.get(2)))).then(7)
                    .when(asset.assetName.containsIgnoreCase(keywordList.get(1))
                            .and(asset.assetName.containsIgnoreCase(keywordList.get(3)))).then(6)
                    .when(asset.assetName.containsIgnoreCase(keywordList.get(2))
                            .and(asset.assetName.containsIgnoreCase(keywordList.get(3)))).then(5)
                    .when(asset.assetName.containsIgnoreCase(keywordList.get(0))).then(4)
                    .when(asset.assetName.containsIgnoreCase(keywordList.get(1))).then(3)
                    .when(asset.assetName.containsIgnoreCase(keywordList.get(2))).then(2)
                    .when(asset.assetName.containsIgnoreCase(keywordList.get(3))).then(1)
                    .otherwise(0);
        }
        return expression;
    }

    public static class OrderByNull extends OrderSpecifier {

        private static final OrderByNull DEFAULT = new OrderByNull();

        private OrderByNull() {
            super(Order.ASC, NullExpression.DEFAULT, NullHandling.Default);
        }

        public static OrderByNull getDefault() {
            return OrderByNull.DEFAULT;
        }
    }
}
