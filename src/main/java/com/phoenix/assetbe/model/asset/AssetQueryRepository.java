package com.phoenix.assetbe.model.asset;

import com.phoenix.assetbe.dto.asset.AssetResponse;
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

import static com.phoenix.assetbe.model.asset.QAsset.asset;
import static com.phoenix.assetbe.model.cart.QCart.cart;
import static com.phoenix.assetbe.model.wish.QWishList.wishList;

@RequiredArgsConstructor
@Repository
public class AssetQueryRepository {
    private final JPAQueryFactory queryFactory;

    public Page<AssetResponse.AssetsOutDTO.AssetDetail> findAssetsWithUserIdAndPaging(Long userId, Pageable pageable){
        List <AssetResponse.AssetsOutDTO.AssetDetail> result = queryFactory
                .select(Projections.constructor(AssetResponse.AssetsOutDTO.AssetDetail.class,
                        asset.id, asset.assetName, asset.price, asset.releaseDate, asset.rating, asset.reviewCount,
                        asset.wishCount, wishList.id, cart.id))
                .from(asset)
                .leftJoin(wishList).on(wishList.asset.id.eq(asset.id)).on(wishList.user.id.eq(userId))
                .leftJoin(cart).on(cart.asset.id.eq(asset.id)).on(cart.user.id.eq(userId))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(assetSort(pageable))
                .fetch();

        Long totalCount = queryFactory.select(asset.count())
                .from(asset)
                .fetchOne();

        return new PageImpl<>(result, pageable, totalCount);
    }

    public Page<AssetResponse.AssetsOutDTO.AssetDetail> findAssetsWithPaging(Pageable pageable){
        List <AssetResponse.AssetsOutDTO.AssetDetail> result = queryFactory
                .select(Projections.constructor(AssetResponse.AssetsOutDTO.AssetDetail.class,
                        asset.id, asset.assetName, asset.price, asset.releaseDate, asset.rating, asset.reviewCount,
                        asset.wishCount))
                .from(asset)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(assetSort(pageable))
                .fetch();

        Long totalCount = queryFactory.select(asset.count())
                .from(asset)
                .fetchOne();

        return new PageImpl<>(result, pageable, totalCount);
    }

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