package com.phoenix.assetbe.model.cart;

import com.phoenix.assetbe.dto.cart.CartResponse;
import com.phoenix.assetbe.model.asset.QAsset;
import com.phoenix.assetbe.model.order.QOrder;
import com.phoenix.assetbe.model.order.QOrderProduct;
import com.phoenix.assetbe.model.user.QUser;
import com.phoenix.assetbe.model.wish.QWishList;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;


import java.util.List;

@RequiredArgsConstructor
@Repository
public class CartQueryRepository {

    private final JPAQueryFactory queryFactory;

    public CartResponse.CountCartOutDTO countByUserId(Long userId) {
        QCart cart = QCart.cart;
        return queryFactory
                .select(Projections.constructor(CartResponse.CountCartOutDTO.class,
                                cart.count()))
                .from(cart)
                .where(cart.user.id.eq(userId))
                .fetchOne();
    }

    public List<CartResponse.GetCartWithOrderOutDTO> getCartWithOrderByUserId(Long userId) {
        QOrder order = QOrder.order;
        QOrderProduct orderProduct = QOrderProduct.orderProduct;
        QCart cart = QCart.cart;
        QAsset asset = QAsset.asset;
        QUser user = QUser.user;
        QWishList wishList = QWishList.wishList;

        return queryFactory
                .select(Projections.constructor(CartResponse.GetCartWithOrderOutDTO.class, cart.id, cart.asset, orderProduct.id, wishList.id))
                .from(cart)
                .leftJoin(order).on(order.user.id.eq(userId))
                .leftJoin(orderProduct).on(order.id.eq(orderProduct.order.id)).on(cart.asset.eq(orderProduct.asset))
                .innerJoin(user).on(user.id.eq(cart.user.id))
                .leftJoin(wishList).on(wishList.user.id.eq(userId)).on(cart.asset.eq(wishList.asset))
                .where(user.id.eq(userId))
                .fetch();
    }
}