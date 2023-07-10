package com.phoenix.assetbe.model.wish;

import com.phoenix.assetbe.dto.wishList.WishResponse;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.phoenix.assetbe.model.asset.QMyAsset.myAsset;
import static com.phoenix.assetbe.model.cart.QCart.cart;
import static com.phoenix.assetbe.model.user.QUser.user;
import static com.phoenix.assetbe.model.wish.QWishList.wishList;

@RequiredArgsConstructor
@Repository
public class WishListQueryRepository {

    private final JPAQueryFactory queryFactory;

    public boolean existsAssetIdAndUserId(Long assetId, Long userId) {
        Integer fetchOne = queryFactory
                .selectOne()
                .from(wishList)
                .where(wishList.asset.id.eq(assetId).and(wishList.user.id.eq(userId)))
                .fetchFirst(); // limit 1
        return fetchOne != null; // 1개가 있는지 없는지 판단 (없으면 null 이므로 null 체크)
    }

    public List<WishResponse.GetWishListWithOrderAndCartOutDTO> getWishListWithOrderAndCartByUserId(Long userId) {

        return queryFactory
                .select(Projections.constructor(WishResponse.GetWishListWithOrderAndCartOutDTO.class, wishList.id, wishList.asset, myAsset.id, cart.id))
                .from(wishList)
                .innerJoin(user).on(user.id.eq(wishList.user.id))
                .leftJoin(myAsset).on(myAsset.asset.eq(wishList.asset)).on(myAsset.user.id.eq(userId))
                .leftJoin(cart).on(cart.user.id.eq(userId)).on(wishList.asset.eq(cart.asset))
                .where(user.id.eq(userId))
                .fetch();
    }
}