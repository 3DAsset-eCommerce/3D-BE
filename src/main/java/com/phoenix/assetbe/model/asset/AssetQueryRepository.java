package com.phoenix.assetbe.model.asset;

import com.phoenix.assetbe.dto.asset.AssetResponse;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

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

    public Page<AssetResponse.AssetListOutDTO.AssetOutDTO> findAssetListWithUserIdAndPaging(Long userId, Pageable pageable){
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

    public Page<AssetResponse.AssetListOutDTO.AssetOutDTO> findAssetListWithPaging(Pageable pageable){
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

    public Page<AssetResponse.AssetListOutDTO.AssetOutDTO> findAssetListWithUserIdAndPaginationByCategory(
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

    public Page<AssetResponse.AssetListOutDTO.AssetOutDTO> findAssetListWithPaginationByCategory(
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

    public Page<AssetResponse.AssetListOutDTO.AssetOutDTO> findAssetListWithUserIdAndPaginationBySubCategory(
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

    public Page<AssetResponse.AssetListOutDTO.AssetOutDTO> findAssetListWithPaginationBySubCategory(
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

    public Optional<Asset> findById(Long userId) {
        return Optional.ofNullable(queryFactory.selectFrom(asset)
                .where(asset.status.eq(true).and(asset.id.eq(userId)))
                .fetchOne());
    }

    public List<Asset> findAllById(List<Long> userIds) {

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(asset.status.eq(true));
        for(Long userId : userIds) {
            builder.or(asset.id.eq(userId));
        }

        return queryFactory.selectFrom(asset)
                .where(builder)
                .fetch();
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
}
