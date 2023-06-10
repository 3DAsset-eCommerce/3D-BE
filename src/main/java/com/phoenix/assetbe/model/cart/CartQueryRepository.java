package com.phoenix.assetbe.model.cart;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@RequiredArgsConstructor
@Repository
public class CartQueryRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public long countByUserId(Long userId) {
        JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);
        QCart cart = QCart.cart;

        return queryFactory.selectFrom(cart)
                .where(cart.user.id.eq(userId))
                .fetchCount();
    }
}