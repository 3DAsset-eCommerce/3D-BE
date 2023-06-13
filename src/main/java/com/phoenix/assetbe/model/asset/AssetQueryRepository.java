package com.phoenix.assetbe.model.asset;

import com.phoenix.assetbe.dto.asset.AssetResponse;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.phoenix.assetbe.model.asset.QAsset.asset;
import static com.phoenix.assetbe.model.cart.QCart.cart;
import static com.phoenix.assetbe.model.wish.QWishList.wishList;

@RequiredArgsConstructor
@Repository
public class AssetQueryRepository {
    private final JPAQueryFactory queryFactory;

    public List<AssetResponse.AssetsOutDTO.AssetDetail> findAssetWithPaging(Long userId){
        return queryFactory
                .select(Projections.constructor(AssetResponse.AssetsOutDTO.AssetDetail.class,
                        asset.id, asset.assetName, asset.price, asset.releaseDate, asset.rating, asset.reviewCount,
                        asset.wishCount, wishList.id, cart.id))
                .from(asset)
                .leftJoin(wishList).on(wishList.asset.id.eq(asset.id)).on(wishList.user.id.eq(userId))
                .leftJoin(cart).on(cart.asset.id.eq(asset.id)).on(cart.user.id.eq(userId))
                .orderBy(asset.releaseDate.desc())
                .fetch();
    }
}
