package com.phoenix.assetbe.model.asset;

import com.phoenix.assetbe.dto.asset.AssetResponse;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.*;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

import static com.phoenix.assetbe.model.asset.QAsset.asset;
import static com.phoenix.assetbe.model.asset.QAssetCategory.assetCategory;
import static com.phoenix.assetbe.model.asset.QAssetTag.assetTag;
import static com.phoenix.assetbe.model.asset.QCategory.category;
import static com.phoenix.assetbe.model.asset.QSubCategory.subCategory;
import static com.phoenix.assetbe.model.cart.QCart.cart;
import static com.phoenix.assetbe.model.wish.QWishList.wishList;

@RequiredArgsConstructor
@Repository
public class AssetQueryRepository {

    private final JPAQueryFactory queryFactory;

    /**
     * 개별 에셋, 로그인 유저, 페이지네이션
     */
    public Page<AssetResponse.AssetListOutDTO.AssetOutDTO> findAssetListWithUserAndPage(Long userId, Pageable pageable){
        List <AssetResponse.AssetListOutDTO.AssetOutDTO> result = queryFactory
                .select(Projections.constructor(AssetResponse.AssetListOutDTO.AssetOutDTO.class,
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
                .leftJoin(wishList).on(wishList.asset.id.eq(asset.id)).on(wishList.user.id.eq(userId))
                .leftJoin(cart).on(cart.asset.id.eq(asset.id)).on(cart.user.id.eq(userId))
                .where(asset.status.eq(true))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(assetSort(pageable))
                .fetch();

        Long totalCount = queryFactory.select(asset.id.countDistinct())
                .from(asset)
                .where(asset.status.eq(true))
                .fetchOne();

        return new PageImpl<>(result, pageable, totalCount);
    }

    /**
     * 개별 에셋, 비로그인 유저, 페이지네이션
     */
    public Page<AssetResponse.AssetListOutDTO.AssetOutDTO> findAssetListWithPage(Pageable pageable){
        List <AssetResponse.AssetListOutDTO.AssetOutDTO> result = queryFactory
                .select(Projections.constructor(AssetResponse.AssetListOutDTO.AssetOutDTO.class,
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
                .where(asset.status.eq(true))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(assetSort(pageable))
                .fetch();

        Long totalCount = queryFactory.select(asset.id.countDistinct())
                .from(asset)
                .where(asset.status.eq(true))
                .fetchOne();

        return new PageImpl<>(result, pageable, totalCount);
    }

    /**
     * 카테고리별 에셋 조회, 로그인 유저, 페이지네이션
     */
    public Page<AssetResponse.AssetListOutDTO.AssetOutDTO> findAssetListWithUserAndPageByCategory(
            Long userId, String categoryName, Pageable pageable){
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
                .innerJoin(assetCategory).on(assetCategory.asset.eq(asset))
                .innerJoin(category).on(assetCategory.category.eq(category))
                .leftJoin(wishList).on(wishList.asset.eq(asset)).on(wishList.user.id.eq(userId))
                .leftJoin(cart).on(cart.asset.eq(asset)).on(cart.user.id.eq(userId))
                .where(category.categoryName.eq(categoryName).and(asset.status.eq(true)))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(assetSort(pageable))
                .fetch();

        Long totalCount = queryFactory.select(asset.id.countDistinct())
                .from(asset)
                .innerJoin(assetCategory).on(assetCategory.asset.eq(asset))
                .innerJoin(category).on(category.eq(assetCategory.category))
                .where(category.categoryName.eq(categoryName).and(asset.status.eq(true)))
                .fetchOne();

        return new PageImpl<>(result, pageable, totalCount);

    }

    /**
     * 카테고리별 에셋 조회, 비로그인 유저, 페이지네이션
     */
    public Page<AssetResponse.AssetListOutDTO.AssetOutDTO> findAssetListWithPageByCategory(
            String categoryName, Pageable pageable){
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
                .innerJoin(assetCategory).on(assetCategory.asset.eq(asset))
                .innerJoin(category).on(assetCategory.category.eq(category))
                .where(category.categoryName.eq(categoryName).and(asset.status.eq(true)))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(assetSort(pageable))
                .fetch();

        Long totalCount = queryFactory.select(asset.id.countDistinct())
                .from(asset)
                .innerJoin(assetCategory).on(assetCategory.asset.eq(asset))
                .innerJoin(category).on(category.eq(assetCategory.category))
                .where(category.categoryName.eq(categoryName).and(asset.status.eq(true)))
                .fetchOne();

        return new PageImpl<>(result, pageable, totalCount);

    }

    /**
     * 카테고리별 에셋 검색, 로그인 유저, 페이지네이션
     */
    public Page<AssetResponse.AssetListOutDTO.AssetOutDTO> findAssetListWithUserAndPageAndSearchByCategory(
            Long userId, String categoryName, List<String> keywordList, Pageable pageable){

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
                .innerJoin(assetCategory).on(assetCategory.asset.id.eq(asset.id))
                .innerJoin(category).on(assetCategory.category.id.eq(category.id))
                .where(category.categoryName.eq(categoryName), asset.status.eq(true), totalCondition(keywordList))
                .leftJoin(wishList).on(wishList.asset.id.eq(asset.id).and(wishList.user.id.eq(userId)))
                .leftJoin(cart).on(cart.asset.id.eq(asset.id).and(cart.user.id.eq(userId)))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(assetSortByIncludedKeywordCount(keywordList).desc(), assetSort(pageable))
                .fetch();

        Long totalCount = queryFactory.select(asset.id.countDistinct())
                .from(asset)
                .innerJoin(assetCategory).on(assetCategory.asset.id.eq(asset.id))
                .innerJoin(category).on(assetCategory.category.id.eq(category.id))
                .where(category.categoryName.eq(categoryName), asset.status.eq(true), totalCondition(keywordList))
                .fetchOne();

        return new PageImpl<>(result, pageable, totalCount);
    }

    /**
     * 카테고리별 에셋 검색, 비로그인 유저, 페이지네이션
     */
    public Page<AssetResponse.AssetListOutDTO.AssetOutDTO> findAssetListWithPageAndSearchByCategory(
            String categoryName, List<String> keywordList,Pageable pageable){

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
                .innerJoin(assetCategory).on(assetCategory.asset.id.eq(asset.id))
                .innerJoin(category).on(assetCategory.category.id.eq(category.id))
                .where(category.categoryName.eq(categoryName), asset.status.eq(true), totalCondition(keywordList))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(assetSortByIncludedKeywordCount(keywordList).desc(), assetSort(pageable))
                .fetch();

        Long totalCount = queryFactory.select(asset.id.countDistinct())
                .from(asset)
                .innerJoin(assetCategory).on(assetCategory.asset.id.eq(asset.id))
                .innerJoin(category).on(assetCategory.category.id.eq(category.id))
                .where(category.categoryName.eq(categoryName), asset.status.eq(true), totalCondition(keywordList))
                .fetchOne();

        return new PageImpl<>(result, pageable, totalCount);

    }

    /**
     * 서브카테고리별 에셋 조회, 로그인 유저, 페이지네이션
     */
    public Page<AssetResponse.AssetListOutDTO.AssetOutDTO> findAssetListWithUserAndPageBySubCategory(
            Long userId, String categoryName, String subCategoryName, Pageable pageable){
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
                .innerJoin(assetTag).on(asset.eq(assetTag.asset))
                .innerJoin(category).on(category.id.eq(assetTag.category.id).and(category.categoryName.eq(categoryName)))
                .innerJoin(subCategory).on(subCategory.id.eq(assetTag.subCategory.id).and(subCategory.subCategoryName.eq(subCategoryName)))
                .leftJoin(wishList).on(wishList.user.id.eq(userId).and(wishList.asset.eq(asset)))
                .leftJoin(cart).on(cart.user.id.eq(userId).and(cart.asset.eq(asset)))
                .where(asset.status.eq(true))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(assetSort(pageable))
                .fetch();

        Long totalCount = queryFactory.select(asset.id.countDistinct())
                .from(asset)
                .innerJoin(assetTag).on(asset.eq(assetTag.asset))
                .innerJoin(category).on(category.id.eq(assetTag.category.id).and(category.categoryName.eq(categoryName)))
                .innerJoin(subCategory).on(subCategory.id.eq(assetTag.subCategory.id).and(subCategory.subCategoryName.eq(subCategoryName)))
                .where(asset.status.eq(true))
                .fetchOne();

        return new PageImpl<>(result, pageable, totalCount);

    }

    /**
     * 서브카테고리별 에셋 조회, 비로그인 유저, 페이지네이션
     */
    public Page<AssetResponse.AssetListOutDTO.AssetOutDTO> findAssetListWithPageBySubCategory(
            String categoryName, String subCategoryName, Pageable pageable){
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
                .innerJoin(assetTag).on(asset.eq(assetTag.asset))
                .innerJoin(category).on(category.id.eq(assetTag.category.id).and(category.categoryName.eq(categoryName)))
                .innerJoin(subCategory).on(subCategory.id.eq(assetTag.subCategory.id).and(subCategory.subCategoryName.eq(subCategoryName)))
                .where(asset.status.eq(true))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(assetSort(pageable))
                .fetch();

        Long totalCount = queryFactory.select(asset.id.countDistinct())
                .from(asset)
                .innerJoin(assetTag).on(asset.eq(assetTag.asset))
                .innerJoin(category).on(category.id.eq(assetTag.category.id).and(category.categoryName.eq(categoryName)))
                .innerJoin(subCategory).on(subCategory.id.eq(assetTag.subCategory.id).and(subCategory.subCategoryName.eq(subCategoryName)))
                .where(asset.status.eq(true))
                .fetchOne();

        return new PageImpl<>(result, pageable, totalCount);

    }

    /**
     * 서브카테고리별 에셋 검색, 로그인 유저, 페이지네이션
     */
    public Page<AssetResponse.AssetListOutDTO.AssetOutDTO> findAssetListWithUserAndPageAndSearchBySubCategory(
            Long userId, String categoryName, String subCategoryName, List<String> keywordList, Pageable pageable){
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
                .innerJoin(assetTag).on(asset.eq(assetTag.asset))
                .innerJoin(category).on(category.id.eq(assetTag.category.id))
                .innerJoin(subCategory).on(subCategory.id.eq(assetTag.subCategory.id))
                .leftJoin(wishList).on(wishList.user.id.eq(userId).and(wishList.asset.eq(asset)))
                .leftJoin(cart).on(cart.user.id.eq(userId).and(cart.asset.eq(asset)))
                .where(category.categoryName.eq(categoryName), subCategory.subCategoryName.eq(subCategoryName), asset.status.eq(true), totalCondition(keywordList))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(assetSortByIncludedKeywordCount(keywordList).desc(), assetSort(pageable))
                .fetch();

        Long totalCount = queryFactory.select(asset.id.countDistinct())
                .from(asset)
                .innerJoin(assetTag).on(asset.eq(assetTag.asset))
                .innerJoin(category).on(category.id.eq(assetTag.category.id))
                .innerJoin(subCategory).on(subCategory.id.eq(assetTag.subCategory.id))
                .where(category.categoryName.eq(categoryName), subCategory.subCategoryName.eq(subCategoryName), asset.status.eq(true), totalCondition(keywordList))
                .fetchOne();

        return new PageImpl<>(result, pageable, totalCount);

    }

    /**
     * 서브카테고리별 에셋 검색, 비로그인 유저, 페이지네이션
     */
    public Page<AssetResponse.AssetListOutDTO.AssetOutDTO> findAssetListWithPageAndSearchBySubCategory(
            String categoryName, String subCategoryName, List<String> keywordList, Pageable pageable){
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
                .innerJoin(assetTag).on(asset.eq(assetTag.asset))
                .innerJoin(category).on(category.id.eq(assetTag.category.id))
                .innerJoin(subCategory).on(subCategory.id.eq(assetTag.subCategory.id))
                .where(category.categoryName.eq(categoryName), subCategory.subCategoryName.eq(subCategoryName), asset.status.eq(true), totalCondition(keywordList))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(assetSortByIncludedKeywordCount(keywordList).desc(), assetSort(pageable))
                .fetch();

        Long totalCount = queryFactory.select(asset.id.countDistinct())
                .from(asset)
                .innerJoin(assetTag).on(asset.eq(assetTag.asset))
                .innerJoin(category).on(category.id.eq(assetTag.category.id))
                .innerJoin(subCategory).on(subCategory.id.eq(assetTag.subCategory.id))
                .where(category.categoryName.eq(categoryName), subCategory.subCategoryName.eq(subCategoryName), asset.status.eq(true), totalCondition(keywordList))
                .fetchOne();

        return new PageImpl<>(result, pageable, totalCount);

    }

    /**
     * 에셋 검색, 로그인 유저, 페이지네이션
     */
    public Page<AssetResponse.AssetListOutDTO.AssetOutDTO> findAssetListWithUserAndPageBySearch(
            Long userId, List<String> keywordList,Pageable pageable){

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
                .where(asset.status.eq(true), totalCondition(keywordList))
                .orderBy(assetSortByIncludedKeywordCount(keywordList).desc(), assetSort(pageable))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long totalCount = queryFactory.select(asset.count())
                .from(asset)
                .where(asset.status.eq(true), totalCondition(keywordList))
                .fetchOne();

        return new PageImpl<>(result, pageable, totalCount);
    }

    /**
     * 에셋 검색, 비로그인 유저, 페이지네이션
     */
    public Page<AssetResponse.AssetListOutDTO.AssetOutDTO> findAssetListWithPageBySearch(
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
                .where(asset.status.eq(true), totalCondition(keywordList))
                .orderBy(assetSortByIncludedKeywordCount(keywordList).desc(), assetSort(pageable))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long totalCount = queryFactory.select(asset.count())
                .from(asset)
                .where(asset.status.eq(true), totalCondition(keywordList))
                .fetchOne();

        return new PageImpl<>(result, pageable, totalCount);
    }

    public Optional<Asset> findById(Long userId) {
        return Optional.ofNullable(queryFactory.selectFrom(asset)
                .where(asset.status.eq(true).and(asset.id.eq(userId)))
                .fetchOne());
    }

    public boolean existsAssetByAssetId(Long assetId) {
        Integer fetchOne = queryFactory
                .selectOne()
                .from(asset)
                .where(asset.status.eq(true).and(asset.id.eq(assetId)))
                .fetchFirst(); // limit 1
        return fetchOne != null; // 1개가 있는지 없는지 판단 (없으면 null 이므로 null 체크)
    }

    private BooleanBuilder totalCondition(List<String> splitKeywordList){
        BooleanBuilder builder = new BooleanBuilder();

        for(String keyword : splitKeywordList){
            builder.or(assetNameLike(keyword));
        }

        return builder;
    }

    private BooleanExpression assetNameLike(String keyword){
        return StringUtils.hasText(keyword) ? asset.assetName.containsIgnoreCase(keyword) : null;
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

        switch (keywordList.size()) {
            case 1:
                expression = new CaseBuilder()
                        .when(asset.assetName.containsIgnoreCase(keywordList.get(0))).then(1)
                        .otherwise(0);
                break;
            case 2:
                expression = new CaseBuilder()
                        .when(asset.assetName.containsIgnoreCase(keywordList.get(0))
                                .and(asset.assetName.containsIgnoreCase(keywordList.get(1)))).then(3)
                        .when(asset.assetName.containsIgnoreCase(keywordList.get(0))).then(2)
                        .when(asset.assetName.containsIgnoreCase(keywordList.get(1))).then(1)
                        .otherwise(0);
                break;
            case 3:
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
                break;
            default:
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
                break;
        }
        return expression;
    }
}
