package com.phoenix.assetbe.model.wish;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

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

    public Long findIdByAssetIdAndUserId(Long assetId, Long userId) {
        return queryFactory
                .select(wishList.id)
                .from(wishList)
                .where(wishList.asset.id.eq(assetId).and(wishList.user.id.eq(userId)))
                .fetchOne();
    }
}